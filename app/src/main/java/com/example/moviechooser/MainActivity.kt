package com.example.moviechooser

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    private lateinit var fetchButton: Button
    private lateinit var movieTextView: TextView
    private lateinit var movieImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchButton = findViewById(R.id.button_fetch)
        movieTextView = findViewById(R.id.textView_movie)
        movieImageView = findViewById(R.id.imageView_movie)

        fetchButton.setOnClickListener {
            fetchRandomMovie()
        }
    }

    private fun fetchRandomMovie() {
        val api = RetrofitClient.instance
        api.getMovies("4c442d6c9d9f9e2d444029fbb1fd7732", "es-ES", "popularity.desc", false, false, "")
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val movies = response.body()!!.results
                        if (movies.isNotEmpty()) {
                            val randomMovie = movies.random()
                            movieTextView.text = randomMovie.title
                            fetchMovieImages(randomMovie.id)
                        } else {
                            movieTextView.text = "No movies found"
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

    private fun fetchMovieImages(movieId: Int) {
        val api = RetrofitClient.instance
        api.getMovieImages(movieId, "4c442d6c9d9f9e2d444029fbb1fd7732")
            .enqueue(object : Callback<MovieImagesResponse> {
                override fun onResponse(call: Call<MovieImagesResponse>, response: Response<MovieImagesResponse>) {
                    if (response.isSuccessful && response.body() != null && response.body()!!.posters.isNotEmpty()) {
                        val imageUrl = "https://image.tmdb.org/t/p/w500${response.body()!!.posters.first().file_path}"
                        Glide.with(this@MainActivity).load(imageUrl).into(movieImageView)
                    } else {
                        movieTextView.text = "No images available for this movie."
                    }
                }

                override fun onFailure(call: Call<MovieImagesResponse>, t: Throwable) {
                    movieTextView.text = "Error fetching images: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }
}

interface TMDbApi {
    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("sort_by") sortBy: String,
        @Query("include_adult") includeAdult: Boolean,
        @Query("include_video") includeVideo: Boolean,
        @Query("with_genres") genres: String
    ): Call<MovieResponse>

    @GET("movie/{movie_id}/images")
    fun getMovieImages(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<MovieImagesResponse>
}

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genre_ids: List<Int>,
    val imageUrl: String  // This field might not be needed if images are fetched separately
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
