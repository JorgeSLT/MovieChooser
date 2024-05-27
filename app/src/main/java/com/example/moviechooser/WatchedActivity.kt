package com.example.moviechooser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.moviechooser.db.AppDatabase
import com.example.moviechooser.db.MovieRating
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchedActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    private var currentSetPos = -1
    private lateinit var watchedMovieIds: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watched)

        // Initialize buttons and set up click listeners for navigation and functionality.
        val btnPrevious = findViewById<Button>(R.id.button_previous)
        val btnNext = findViewById<Button>(R.id.button_next)
        val btnHome = findViewById<ImageButton>(R.id.button_home)
        val btnWatchlistPage = findViewById<Button>(R.id.button_watchlist_page)

        db = AppDatabase.getDatabase(this)

        loadWatchedMovieIds()
        updateMovieDetails()

        // Navigate to the previous movie in the watched list.
        btnPrevious.setOnClickListener {
            if (currentSetPos < watchedMovieIds.size - 1) {
                currentSetPos++
                updateMovieDetails()
            }
        }

        // Navigate to the next movie in the watched list.
        btnNext.setOnClickListener {
            if (currentSetPos > 0) {
                currentSetPos--
                updateMovieDetails()
            }
        }

        // Navigate back to the main activity.
        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Navigate to the watchlist activity.
        btnWatchlistPage.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the rating bar for movie ratings and handle user input.
        val movieRatingBar = findViewById<RatingBar>(R.id.movieRatingBar)
        movieRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                updateMovieRating(watchedMovieIds[currentSetPos], rating.toInt())
            }
        }

        // Navigate to the language settings activity.
        val btnProfile = findViewById<ImageButton>(R.id.button_profile)
        btnProfile.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Share movie details with external apps.
        val btnShare = findViewById<ImageButton>(R.id.button_share)
        btnShare.setOnClickListener {
            shareMovieDetails()
        }

    }


    private fun showRatingDialog() {
        val ratings = arrayOf("1", "2", "3", "4", "5")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.rate_movie))
        builder.setItems(ratings) { _, which ->
            val rating = ratings[which].toInt()
            updateMovieRating(watchedMovieIds[currentSetPos], rating)
        }
        builder.create().show()
    }

    // Share movie details via other apps.
    private fun shareMovieDetails() {
        if (currentSetPos >= 0 && currentSetPos < watchedMovieIds.size) {
            val movieTitleView = findViewById<TextView>(R.id.textView_movie_title)
            val movieTitle = movieTitleView.text.toString()
            val movieRatingBar = findViewById<RatingBar>(R.id.movieRatingBar)
            val movieRating = movieRatingBar.rating.toInt()

            val shareMessage = getString(R.string.share_movie_message, movieTitle, movieRating)
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } else {
            Toast.makeText(this, getString(R.string.no_movie_info), Toast.LENGTH_SHORT).show()
        }
    }


    // Update movie rating in the database.
    private fun updateMovieRating(movieId: String, rating: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val movieRatingDao = db.movieRatingDao()
            movieRatingDao.insertRating(MovieRating(movieId.toInt(), rating))
            fetchMovieRating(movieId)
        }
    }

    // Fetch and display the current movie rating from the database.
    private fun fetchMovieRating(movieId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val rating = db.movieRatingDao().getRatingById(movieId.toInt())
            withContext(Dispatchers.Main) {
                findViewById<RatingBar>(R.id.movieRatingBar).rating = rating?.toFloat() ?: 0f
            }
        }
    }

    // Update the UI with details of the current movie.
    private fun updateMovieDetails() {
        if (currentSetPos >= 0 && currentSetPos < watchedMovieIds.size) {
            val movieId = watchedMovieIds[currentSetPos]
            fetchMovieDetailsById(movieId)
            fetchMovieImages(movieId.toInt())
            fetchMovieRating(movieId)
        }

        if (watchedMovieIds.isEmpty()) {
            findViewById<TextView>(R.id.textView_movie_title).text = getString(R.string.no_movies_watched)
            findViewById<ImageView>(R.id.imageView_watched_movie).setImageResource(0) // Clear the image view
        } else {
            if (currentSetPos >= 0 && currentSetPos < watchedMovieIds.size) {
                val movieId = watchedMovieIds[currentSetPos]
                fetchMovieDetailsById(movieId)
                fetchMovieImages(movieId.toInt())
                fetchMovieRating(movieId)
            }
        }
    }

    // Load watched movie IDs from shared preferences.
    private fun loadWatchedMovieIds(): List<String> {
        val sharedPreferences = getSharedPreferences("MovieChooserPrefs", Context.MODE_PRIVATE)
        val watchedMoviesSet = sharedPreferences.getStringSet("watchedMovies", emptySet()) ?: emptySet()
        watchedMovieIds = watchedMoviesSet.toList()
        currentSetPos = watchedMovieIds.size - 1
        return watchedMovieIds
    }

    // Fetch movie details from the database or API and update the UI.
    private fun fetchMovieDetailsById(movieId: String) {
        val api = RetrofitClient.instance
        api.getMovieByID(movieId.toInt(), "4c442d6c9d9f9e2d444029fbb1fd7732").enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful && response.body() != null) {
                    val movie = response.body()!!
                    findViewById<TextView>(R.id.textView_movie_title).text = movie.title
                } else {
                    findViewById<TextView>(R.id.textView_movie_title).text = getString(R.string.movie_not_found)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                findViewById<TextView>(R.id.textView_movie_title).text = "Error fetching movie details: ${t.localizedMessage ?: "Unknown error"}"
            }
        })
    }

    // Fetch movie images from the API and update the UI.
    private fun fetchMovieImages(movieId: Int) {
        val api = RetrofitClient.instance
        api.getMovieImages(movieId, "4c442d6c9d9f9e2d444029fbb1fd7732").enqueue(object : Callback<MovieImagesResponse> {
            override fun onResponse(call: Call<MovieImagesResponse>, response: Response<MovieImagesResponse>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.posters.isNotEmpty()) {
                    val imageUrl = "https://image.tmdb.org/t/p/w500${response.body()!!.posters.first().file_path}"
                    Glide.with(this@WatchedActivity).load(imageUrl).into(findViewById<ImageView>(R.id.imageView_watched_movie))
                } else {
                    findViewById<TextView>(R.id.textView_movie_title).text = "No images available"
                }
            }

            override fun onFailure(call: Call<MovieImagesResponse>, t: Throwable) {
                findViewById<TextView>(R.id.textView_movie_title).text = "Error fetching images: ${t.localizedMessage ?: "Unknown error"}"
            }
        })
    }
}

