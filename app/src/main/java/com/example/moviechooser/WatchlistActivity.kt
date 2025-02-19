package com.example.moviechooser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.moviechooser.db.AppDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchlistActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    private var currentSetPos = -1
    private lateinit var watchlistMovieIds: List<String>

    // Lazy initialization of sets to hold IDs of watched and watchlist movies from SharedPreferences.
    private val watchedMovies: MutableSet<String> by lazy {
        getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchedMovies", mutableSetOf()) ?: mutableSetOf()
    }

    private val watchlistMovies: MutableSet<String> by lazy {
        getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchlistMovies", mutableSetOf()) ?: mutableSetOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)

        // Initialize buttons and set up click listeners for navigation and interaction.
        val btnPrevious = findViewById<Button>(R.id.button_previous)
        val btnNext = findViewById<Button>(R.id.button_next)
        val btnWatched = findViewById<ImageButton>(R.id.button_mark_watched)
        val btnHome = findViewById<ImageButton>(R.id.button_home)
        val btnWatchedPage = findViewById<Button>(R.id.button_watched_page)
        val btnProfile = findViewById<ImageButton>(R.id.button_profile)

        db = AppDatabase.getDatabase(this)

        loadWatchlistMovieIds()
        updateMovieDetails()

        // Navigate to the previous movie in the watchlist.
        btnPrevious.setOnClickListener {
            if (currentSetPos < watchlistMovieIds.size - 1) {
                currentSetPos++
                updateMovieDetails()
            }
        }

        // Navigate to the next movie in the watchlist.
        btnNext.setOnClickListener {
            if (currentSetPos > 0) {
                currentSetPos--
                updateMovieDetails()
            }
        }

        // Mark the current movie as watched and update local storage.
        btnWatched.setOnClickListener {
            markMovieAsWatched()
        }

        // Return to the main activity.
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Navigate to the watched movies page.
        btnWatchedPage.setOnClickListener {
            val intent = Intent(this, WatchedActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Navigate to the language settings page.
        btnProfile.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnMovies = findViewById<ImageButton>(R.id.button_movies)
        btnMovies.setOnClickListener {
            val intent = Intent(this, WatchedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Mark the current movie as watched, updating both watched and watchlist sets.
    private fun markMovieAsWatched() {
        val movieId = watchlistMovieIds[currentSetPos]
        if (movieId.isNotEmpty()) {
            // Añadir a la lista de películas vistas
            watchedMovies.add(movieId)
            getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).edit()
                .putStringSet("watchedMovies", watchedMovies).apply()

            // Eliminar de la lista de watchlist
            val tempWatchlist = watchlistMovies.toMutableSet()
            tempWatchlist.remove(movieId)
            getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).edit()
                .putStringSet("watchlistMovies", tempWatchlist).apply()

            // Actualiza la interfaz de usuario
            findViewById<TextView>(R.id.textView_movie_title).text = getString(R.string.marked_watchlist)

            // Actualizar la lista de IDs en memoria
            watchlistMovieIds = tempWatchlist.toList()
            if (currentSetPos >= watchlistMovieIds.size) {
                currentSetPos = watchlistMovieIds.size - 1
            }
            updateMovieDetails()
        }
    }

    // Update UI with the details of the current movie from the watchlist.
    private fun updateMovieDetails() {
        if (watchlistMovieIds.isEmpty()) {
            findViewById<TextView>(R.id.textView_movie_title).text = getString(R.string.no_watchlist)
            findViewById<ImageView>(R.id.imageView_watched_movie).setImageResource(0) // Clear the image view
            findViewById<ImageButton>(R.id.button_mark_watched).visibility = View.GONE // Hide the button
        } else {
            findViewById<ImageButton>(R.id.button_mark_watched).visibility = View.VISIBLE // Show the button
            if (currentSetPos >= 0 && currentSetPos < watchlistMovieIds.size) {
                fetchMovieDetailsById(watchlistMovieIds[currentSetPos])
                fetchMovieImages(watchlistMovieIds[currentSetPos].toInt())
            }
        }
    }

    // Load movie IDs from SharedPreferences into the watchlist.
    private fun loadWatchlistMovieIds(): List<String> {
        val sharedPreferences = getSharedPreferences("MovieChooserPrefs", Context.MODE_PRIVATE)
        val watchlistMoviesSet = sharedPreferences.getStringSet("watchlistMovies", emptySet()) ?: emptySet()
        watchlistMovieIds = watchlistMoviesSet.toList()
        currentSetPos = watchlistMovieIds.size - 1
        return watchlistMovieIds
    }

    // Fetch movie details from the API by movie ID.
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

    // Fetch movie images from the API and update the ImageView.
    private fun fetchMovieImages(movieId: Int) {
        val api = RetrofitClient.instance
        api.getMovieImages(movieId, "4c442d6c9d9f9e2d444029fbb1fd7732").enqueue(object : Callback<MovieImagesResponse> {
            override fun onResponse(call: Call<MovieImagesResponse>, response: Response<MovieImagesResponse>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.posters.isNotEmpty()) {
                    val imageUrl = "https://image.tmdb.org/t/p/w500${response.body()!!.posters.first().file_path}"
                    Glide.with(this@WatchlistActivity).load(imageUrl).into(findViewById<ImageView>(R.id.imageView_watched_movie))
                } else {
                    findViewById<TextView>(R.id.textView_movie_title).text = getString(R.string.no_images)
                }
            }

            override fun onFailure(call: Call<MovieImagesResponse>, t: Throwable) {
                findViewById<TextView>(R.id.textView_movie_title).text = "Error fetching images: ${t.localizedMessage ?: "Unknown error"}"
            }
        })
    }
}

