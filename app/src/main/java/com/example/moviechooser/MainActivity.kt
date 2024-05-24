package com.example.moviechooser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    private lateinit var fetchButton: Button
    private lateinit var movieTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchButton = findViewById(R.id.button_fetch)
        movieTextView = findViewById(R.id.textView_movie)

        fetchButton.setOnClickListener {
            fetchRandomMovie()
        }
    }

    private fun fetchRandomMovie() {
        val api = RetrofitClient.instance
        api.getMovies("4c442d6c9d9f9e2d444029fbb1fd7732", "es-ES", "popularity.desc", false, false, 1, "")
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val movie = response.body()!!.results.random() // Get a random movie from the list
                        movieTextView.text = movie.title
                    } else {
                        movieTextView.text = "Failed to retrieve movies: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    movieTextView.text = "Error in fetching movies: ${t.message}"
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
        @Query("page") page: Int,
        @Query("with_genres") genres: String
    ): Call<MovieResponse>
}

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genre_ids: List<Int>
)

data class MovieResponse(
    val results: List<Movie>
)

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val instance: TMDbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDbApi::class.java)
    }
}
