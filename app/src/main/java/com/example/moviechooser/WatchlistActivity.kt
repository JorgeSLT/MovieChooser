package com.example.moviechooser

import android.content.Context
import android.os.Bundle
import android.widget.Button
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

class WatchlistActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    private var currentSetPos = -1
    private lateinit var watchlistMovieIds: List<String>

    private val watchedMovies: MutableSet<String> by lazy {
        getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchedMovies", mutableSetOf()) ?: mutableSetOf()
    }

    private val watchlistMovies: MutableSet<String> by lazy {
        getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchlistMovies", mutableSetOf()) ?: mutableSetOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)

        val btnPrevious = findViewById<Button>(R.id.button_previous)
        val btnNext = findViewById<Button>(R.id.button_next)
        val btnWatched = findViewById<Button>(R.id.button_mark_watched)

        db = AppDatabase.getDatabase(this)

        loadWatchlistMovieIds()
        updateMovieDetails()

        btnPrevious.setOnClickListener {
            if (currentSetPos < watchlistMovieIds.size - 1) {
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

        btnWatched.setOnClickListener {
            markMovieAsWatched()
        }
    }

    private fun markMovieAsWatched() {
        val movieId = watchlistMovieIds[currentSetPos].toInt()
        if (movieId != -1) {
            watchedMovies.add(movieId.toString())
            getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).edit()
                .putStringSet("watchedMovies", watchedMovies).apply()
            findViewById<TextView>(R.id.textView_movie_title).text = "Marked as watched"
        }
    }

    private fun updateMovieDetails() {
        if (currentSetPos >= 0 && currentSetPos < watchlistMovieIds.size) {
            val movieId = watchlistMovieIds[currentSetPos]
            fetchMovieDetailsById(movieId)
            fetchMovieImages(movieId.toInt())
        }
    }

    private fun loadWatchlistMovieIds(): List<String> {
        val sharedPreferences = getSharedPreferences("MovieChooserPrefs", Context.MODE_PRIVATE)
        val watchlistMoviesSet = sharedPreferences.getStringSet("watchlistMovies", emptySet()) ?: emptySet()
        watchlistMovieIds = watchlistMoviesSet.toList()
        currentSetPos = watchlistMovieIds.size - 1
        return watchlistMovieIds
    }

    private fun fetchMovieDetailsById(movieId: String) {
        val api = RetrofitClient.instance
        api.getMovieByID(movieId.toInt(), "4c442d6c9d9f9e2d444029fbb1fd7732").enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful && response.body() != null) {
                    val movie = response.body()!!
                    findViewById<TextView>(R.id.textView_movie_title).text = movie.title
                } else {
                    findViewById<TextView>(R.id.textView_movie_title).text = "Movie not found"
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
                    Glide.with(this@WatchlistActivity).load(imageUrl).into(findViewById<ImageView>(R.id.imageView_watched_movie))
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

