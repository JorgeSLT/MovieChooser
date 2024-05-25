package com.example.moviechooser

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchedActivity : AppCompatActivity() {

    private var currentSetPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watched)

        loadWatchedMovieIds()
        if (currentSetPos != -1) {
            fetchMovieInPos(currentSetPos)
        }
    }

    private lateinit var watchedMovieIds: List<String>

    private fun loadWatchedMovieIds(): List<String> {
        val sharedPreferences = getSharedPreferences("MovieChooserPrefs", Context.MODE_PRIVATE)
        val watchedMoviesSet = sharedPreferences.getStringSet("watchedMovies", emptySet()) ?: emptySet()
        watchedMovieIds = watchedMoviesSet.toList()
        currentSetPos = watchedMovieIds.size - 1
        return watchedMovieIds
    }

    private fun fetchMovieDetailsById(movieId: String) {
        val api = RetrofitClient.instance
        api.getMovieByID(movieId.toInt(), "4c442d6c9d9f9e2d444029fbb1fd7732")
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                    if (response.isSuccessful && response.body() != null) {
                        val movie = response.body()!!
                        findViewById<TextView>(R.id.textView_movie_title).text = movie.title
                    } else {
                        findViewById<TextView>(R.id.textView_movie_title).text = movieId
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                    findViewById<TextView>(R.id.textView_movie_title).text = "Error fetching movie details: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }

    private fun fetchMovieImages(movieId: Int) {
        val api = RetrofitClient.instance
        api.getMovieImages(movieId, "4c442d6c9d9f9e2d444029fbb1fd7732")
            .enqueue(object : Callback<MovieImagesResponse> {
                override fun onResponse(call: Call<MovieImagesResponse>, response: Response<MovieImagesResponse>) {
                    if (response.isSuccessful && response.body() != null && response.body()!!.posters.isNotEmpty()) {
                        val imageUrl = "https://image.tmdb.org/t/p/w500${response.body()!!.posters.first().file_path}"
                        Glide.with(this@WatchedActivity).load(imageUrl).into(findViewById(R.id.imageView_watched_movie))
                    } else {
                        findViewById<TextView>(R.id.textView_movie_title).text = "No images available for this movie."
                    }
                }

                override fun onFailure(call: Call<MovieImagesResponse>, t: Throwable) {
                    findViewById<TextView>(R.id.textView_movie_title).text = "Error fetching images: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }

    private fun fetchMovieInPos(position: Int) {
        if (position in watchedMovieIds.indices) {
            fetchMovieDetailsById(watchedMovieIds[position])
            fetchMovieImages(watchedMovieIds[position].toInt())
        } else {
            findViewById<TextView>(R.id.textView_movie_title).text = "Invalid movie position."
        }
    }
}
