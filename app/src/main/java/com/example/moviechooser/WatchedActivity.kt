package com.example.moviechooser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

    private lateinit var movieRatingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watched)

        val btnPrevious = findViewById<Button>(R.id.button_previous)
        val btnNext = findViewById<Button>(R.id.button_next)
        val btnUpdateRating = findViewById<Button>(R.id.button_update_rating)
        val btnHome = findViewById<ImageButton>(R.id.button_home)
        val btnWatchlistPage = findViewById<Button>(R.id.button_watchlist_page)
        val btnLanguage = findViewById<Button>(R.id.button_language_page)

        movieRatingTextView = findViewById<TextView>(R.id.textView_movie_rating)

        db = AppDatabase.getDatabase(this)

        loadWatchedMovieIds()
        updateMovieDetails()

        btnPrevious.setOnClickListener {
            if (currentSetPos < watchedMovieIds.size - 1) {
                currentSetPos++
                updateMovieDetails()
            }
        }

        btnNext.setOnClickListener {
            if (currentSetPos > 0) {
                currentSetPos--
                updateMovieDetails()
            }
        }

        btnUpdateRating.setOnClickListener {
            showRatingDialog()
        }

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnWatchlistPage.setOnClickListener {
            val intent = Intent(this, WatchlistActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLanguage.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun updateMovieRating(movieId: String, rating: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val movieRatingDao = db.movieRatingDao()
            movieRatingDao.insertRating(MovieRating(movieId.toInt(), rating))
            fetchMovieRating(movieId)
        }
    }

    private fun fetchMovieRating(movieId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val rating = db.movieRatingDao().getRatingById(movieId.toInt())
            val ratedText = rating?.toString() ?: getString(R.string.rating_not_available)
            withContext(Dispatchers.Main) {
                movieRatingTextView.text = getString(R.string.label_rating, ratedText)
            }
        }
    }

    private fun updateMovieDetails() {
        if (currentSetPos >= 0 && currentSetPos < watchedMovieIds.size) {
            val movieId = watchedMovieIds[currentSetPos]
            fetchMovieDetailsById(movieId)
            fetchMovieImages(movieId.toInt())
            fetchMovieRating(movieId)
        }

        if (watchedMovieIds.isEmpty()) {
            findViewById<TextView>(R.id.textView_movie_title).text = getString(R.string.no_movies_watched)
            findViewById<TextView>(R.id.textView_movie_rating).text = ""
            findViewById<ImageView>(R.id.imageView_watched_movie).setImageResource(0) // Clear the image view
            findViewById<Button>(R.id.button_update_rating).visibility = View.GONE // Hide the button
        } else {
            findViewById<Button>(R.id.button_update_rating).visibility = View.VISIBLE // Show the button
            if (currentSetPos >= 0 && currentSetPos < watchedMovieIds.size) {
                val movieId = watchedMovieIds[currentSetPos]
                fetchMovieDetailsById(movieId)
                fetchMovieImages(movieId.toInt())
                fetchMovieRating(movieId)
            }
        }
    }

    private fun loadWatchedMovieIds(): List<String> {
        val sharedPreferences = getSharedPreferences("MovieChooserPrefs", Context.MODE_PRIVATE)
        val watchedMoviesSet = sharedPreferences.getStringSet("watchedMovies", emptySet()) ?: emptySet()
        watchedMovieIds = watchedMoviesSet.toList()
        currentSetPos = watchedMovieIds.size - 1
        return watchedMovieIds
    }

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

