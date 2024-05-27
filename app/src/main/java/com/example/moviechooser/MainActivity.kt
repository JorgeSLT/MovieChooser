package com.example.moviechooser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.moviechooser.db.AppDatabase
import com.example.moviechooser.db.MovieRating

class MainActivity : AppCompatActivity() {
    // UI components declaration
    private lateinit var fetchButton: Button
    private lateinit var movieTextView: TextView
    private lateinit var movieImageView: ImageView
    private lateinit var selectGenreButton: ImageButton
    private lateinit var watchedButton: Button
    private lateinit var watchlistMovieButton: Button
    private lateinit var profileButton: ImageButton

    // Variables to hold selected filter values
    private var selectedGenre: String = ""
    private var minRating: Float = 0f
    private var currentMovieId: Int = -1
    private var releaseYear: Int = 0
    private var includeAdult: Boolean = false

    // Database access
    private lateinit var db: AppDatabase

    // Preferences to keep track of watched and watchlisted movies
    private val watchedMovies: MutableSet<String> by lazy {
        getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchedMovies", mutableSetOf()) ?: mutableSetOf()
    }

    private val watchlistMovies: MutableSet<String> by lazy {
        getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchlistMovies", mutableSetOf()) ?: mutableSetOf()
    }

    // Map of movie genres for filtering
    private val genreMap = mapOf(
        "Action" to "28",
        "Adventure" to "12",
        "Animation" to "16",
        "Comedy" to "35",
        "Crime" to "80",
        "Documentary" to "99",
        "Drama" to "18",
        "Family" to "10751",
        "Fantasy" to "14",
        "History" to "36",
        "Horror" to "27",
        "Music" to "10402",
        "Mystery" to "9648",
        "Romance" to "10749",
        "Science Fiction" to "878",
        "TV Movie" to "10770",
        "Thriller" to "53",
        "War" to "10752",
        "Western" to "37"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components and set click listeners
        fetchButton = findViewById(R.id.button_fetch)
        profileButton = findViewById(R.id.button_profile)
        watchedButton = findViewById(R.id.button_mark_watched)
        selectGenreButton = findViewById(R.id.button_select_genre)
        movieTextView = findViewById(R.id.textView_movie)
        movieImageView = findViewById(R.id.imageView_movie)
        watchlistMovieButton = findViewById(R.id.button_mark_watchlist)

        if (isFirstTime()) {
            lifecycleScope.launch(Dispatchers.IO) {
                initializeDatabase(applicationContext)
            }
        }

        selectGenreButton.setOnClickListener {
            showFilterDialog()
        }

        fetchButton.setOnClickListener {
            fetchRandomMovie()
        }

        watchedButton.setOnClickListener {
            markMovieAsWatched()
        }

        watchlistMovieButton.setOnClickListener {
            markMovieAsWatchlist()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        val btnMovies = findViewById<ImageButton>(R.id.button_movies)
        btnMovies.setOnClickListener {
            val intent = Intent(this, WatchedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Check if it's the first time the app has been launched
    private fun isFirstTime(): Boolean {
        val sharedPref = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("FIRST_TIME", true)
    }

    // Mark a movie as watched and update preferences
    private fun markMovieAsWatched() {
        if (currentMovieId != -1) {
            watchedMovies.add(currentMovieId.toString())
            getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).edit()
                .putStringSet("watchedMovies", watchedMovies).apply()
            Toast.makeText(this, getString(R.string.mark_as_watched), Toast.LENGTH_SHORT).show()
        }
    }

    // Mark a movie as part of the watchlist and update preferences
    private fun markMovieAsWatchlist() {
        if (currentMovieId != -1) {
            watchlistMovies.add(currentMovieId.toString())
            getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).edit()
                .putStringSet("watchlistMovies", watchlistMovies).apply()
            Toast.makeText(this, getString(R.string.added_to_watchlist), Toast.LENGTH_SHORT).show()
        }
    }

    // Show the main filter selection dialog
    private fun showFilterDialog() {
        val filterCategories = arrayOf(getString(R.string.genre), getString(R.string.rating), getString(R.string.year), getString(R.string.include_adult))
        val adapter = ArrayAdapter<String>(this, R.layout.dialog_item, filterCategories)

        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.select_filter))

        builder.setAdapter(adapter) { dialog, which ->
            when (filterCategories[which]) {
                getString(R.string.genre) -> showGenreDialog()
                getString(R.string.rating) -> showRatingFilterDialog()
                getString(R.string.year) -> showReleaseYearDialog()
                getString(R.string.include_adult) -> toggleAdultContent()
            }
        }
        builder.setNegativeButton("Go Back") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    // Toggle content filter for adult content and reflect changes
    private fun toggleAdultContent() {
        val originalIncludeAdult = includeAdult
        val options = arrayOf(getString(R.string.include), getString(R.string.exclude))
        val adapter = ArrayAdapter<String>(this, R.layout.dialog_item, options)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.include_adult))
        builder.setSingleChoiceItems(adapter, if (includeAdult) 0 else 1) { dialog, which ->
            includeAdult = which == 0
        }
        builder.setNeutralButton(getString(R.string.back_changes)) { _, _ ->
            includeAdult = originalIncludeAdult
        }
        builder.setNegativeButton(getString(R.string.back)) { dialog, _ ->
            showFilterDialog()
        }
        builder.show()
    }

    // Filter dialog for selecting movie rating
    private fun showRatingFilterDialog() {
        val originalRating = minRating
        val ratings = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val adapter = ArrayAdapter<String>(this, R.layout.dialog_item, ratings)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.select_rating))
        builder.setSingleChoiceItems(adapter, ratings.indexOf(minRating.toString())) { dialog, which ->
            minRating = ratings[which].toFloat()
        }
        builder.setNeutralButton(getString(R.string.back_changes)) { _, _ ->
            minRating = originalRating
        }
        builder.setNegativeButton(getString(R.string.back)) { dialog, _ ->
            showFilterDialog()
        }
        builder.show()
    }

    // Filter dialog for selecting movie release year
    private fun showReleaseYearDialog() {
        val originalYear = releaseYear
        val years = (1990..2024).map { it.toString() }.toList().toTypedArray()
        val adapter = ArrayAdapter<String>(this, R.layout.dialog_item, years)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.select_year))
        builder.setSingleChoiceItems(adapter, years.indexOf(releaseYear.toString())) { dialog, which ->
            releaseYear = years[which].toInt()
        }
        builder.setNeutralButton(getString(R.string.back_changes)) { _, _ ->
            releaseYear = originalYear
        }
        builder.setNegativeButton(getString(R.string.back)) { dialog, _ ->
            showFilterDialog()
        }
        builder.show()
    }

    // Dialog for selecting movie genre
    private fun showGenreDialog() {
        val originalGenre = selectedGenre
        val genres = genreMap.keys.toTypedArray()
        val adapter = ArrayAdapter<String>(this, R.layout.dialog_item, genres)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.choose_genre))
        builder.setSingleChoiceItems(adapter, genres.indexOf(selectedGenre)) { dialog, which ->
            selectedGenre = genreMap[genres[which]] ?: ""
        }
        builder.setNeutralButton(getString(R.string.back_changes)) { _, _ ->
            selectedGenre = originalGenre
        }
        builder.setNegativeButton(getString(R.string.back)) { dialog, _ ->
            showFilterDialog()
        }
        builder.show()
    }

    // Rating dialog for movie rating
    private fun showRatingDialog() {
        val ratings = arrayOf("1", "2", "3", "4", "5")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.rate_movie))
        builder.setItems(ratings) { _, which ->
            saveRating(ratings[which].toInt())
        }
        builder.create().show()
    }

    private fun saveRating(rating: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val movieDao = db.movieRatingDao()
            movieDao.insertRating(MovieRating(currentMovieId, rating))
            withContext(Dispatchers.Main) {
                movieTextView.append(getString(R.string.rated, rating))
            }
        }
    }

    // Fetch random movie from API based on selected filters
    private fun fetchRandomMovie() {
        val api = RetrofitClient.instance
        api.getMovies("4c442d6c9d9f9e2d444029fbb1fd7732", "es-ES", "popularity.desc", includeAdult = includeAdult, false, selectedGenre, voteAverageGte = minRating.toString(), year = releaseYear)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val movies = response.body()!!.results.filterNot { movie ->
                            watchedMovies.contains(movie.id.toString())
                        }
                        if (movies.isNotEmpty()) {
                            val randomMovie = movies.random()
                            currentMovieId = randomMovie.id
                            movieTextView.text = randomMovie.title
                            fetchMovieImages(randomMovie.id)
                        } else {
                            movieTextView.text = getString(R.string.no_movies)
                        }
                    } else {
                        movieTextView.text = "Failed to retrieve movies: ${response.errorBody()?.string() ?: "Unknown error"}"
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    movieTextView.text = "Error in fetching movies: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }

    // Fetch and display movie images from API
    private fun fetchMovieImages(movieId: Int) {
        val api = RetrofitClient.instance
        api.getMovieImages(movieId, "4c442d6c9d9f9e2d444029fbb1fd7732")
            .enqueue(object : Callback<MovieImagesResponse> {
                override fun onResponse(call: Call<MovieImagesResponse>, response: Response<MovieImagesResponse>) {
                    if (response.isSuccessful && response.body() != null && response.body()!!.posters.isNotEmpty()) {
                        val imageUrl = "https://image.tmdb.org/t/p/w500${response.body()!!.posters.first().file_path}"
                        Glide.with(this@MainActivity).load(imageUrl).into(movieImageView)
                    } else {
                        movieTextView.text = getString(R.string.no_images)
                    }
                }

                override fun onFailure(call: Call<MovieImagesResponse>, t: Throwable) {
                    movieTextView.text = "Error fetching images: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }

    // Initialize the database
    private fun initializeDatabase(context: Context) {
        db = AppDatabase.getDatabase(context)
    }
}

//Everything needed to stablish easy connection for API requests
interface TMDbApi {
    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("sort_by") sortBy: String,
        @Query("include_adult") includeAdult: Boolean,
        @Query("include_video") includeVideo: Boolean,
        @Query("with_genres") genres: String,
        @Query("vote_average.gte") voteAverageGte: String,
        @Query("year") year: Int
    ): Call<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieByID(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
    ): Call<Movie>

    @GET("movie/{movie_id}/images")
    fun getMovieImages(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<MovieImagesResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    val instance: TMDbApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TMDbApi::class.java)
    }
}

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genre_ids: List<Int>,
    val imageUrl: String
)

data class MovieResponse(
    val results: List<Movie>
)

data class MovieImagesResponse(
    val id: Int,
    val backdrops: List<ImageDetail>,
    val posters: List<ImageDetail>
)

data class ImageDetail(
    val file_path: String
)

class GenreAdapter(context: Context, genres: Array<String>) : ArrayAdapter<String>(context, 0, genres) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.dialog_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = getItem(position)
        return view
    }
}
