package com.example.moviechooser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    private lateinit var fetchButton: Button
    private lateinit var movieTextView: TextView
    private lateinit var viewPager: ViewPager2
    private var imagesList: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchButton = findViewById(R.id.button_fetch)
        movieTextView = findViewById(R.id.textView_movie)
        viewPager = findViewById(R.id.viewPagerImages)

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

    private fun fetchRandomImages(finalImageUrl: String?) {
        val api = RetrofitClient.instance
        api.getPopularMovies("4c442d6c9d9f9e2d444029fbb1fd7732", "es-ES", 1)  // Considera iterar sobre múltiples páginas si es necesario
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val images = response.body()!!.results
                            .take(5)  // Tomamos las primeras 5 imágenes
                            .map { "https://image.tmdb.org/t/p/w500${it.imageUrl}" }
                            .toMutableList()

                        println("First 5 Images: $images")  // Log para verificar las imágenes

                        if (finalImageUrl != null) {
                            images.add(finalImageUrl)  // Añade la imagen final
                            println("Final Image Added: $finalImageUrl")  // Log para verificar la imagen final
                        }
                        setupViewPager(images, finalImageUrl)
                    } else {
                        println("Failed to retrieve first 5 images: ${response.errorBody()?.string()}")  // Log del error
                        movieTextView.text = "Failed to retrieve images: ${response.errorBody()?.string() ?: "Unknown error"}"
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    println("API Call Failure: ${t.message}")  // Log del fallo de la API
                    movieTextView.text = "Error in fetching images: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }




    private fun fetchMovieImages(movieId: Int) {
        val api = RetrofitClient.instance
        api.getMovieImages(movieId, "4c442d6c9d9f9e2d444029fbb1fd7732")
            .enqueue(object : Callback<MovieImagesResponse> {
                override fun onResponse(call: Call<MovieImagesResponse>, response: Response<MovieImagesResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val finalImage = response.body()!!.posters
                            .map { "https://image.tmdb.org/t/p/w500${it.file_path}" }
                            .firstOrNull()
                        fetchRandomImages(finalImage)
                    } else {
                        movieTextView.text = "Failed to retrieve images: ${response.errorBody()?.string() ?: "Unknown error"}"
                    }
                }

                override fun onFailure(call: Call<MovieImagesResponse>, t: Throwable) {
                    movieTextView.text = "Error in fetching images: ${t.localizedMessage ?: "Unknown error"}"
                }
            })
    }


    private fun setupViewPager(images: List<String>, finalImageUrl: String?) {
        val adapter = ImageSliderAdapter(images, finalImageUrl)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(0, true)  // Asegúrate de que empieza en la primera imagen

        // Opcional: Agrega un delay antes de mover al final para asegurar que las imágenes se muestren
        Handler(Looper.getMainLooper()).postDelayed({
            finalImageUrl?.let {
                val finalIndex = images.indexOf(it)
                if (finalIndex != -1) {
                    viewPager.setCurrentItem(finalIndex, true)
                }
            }
        }, 5000)  // 5000 ms = 5 segundos antes de mostrar la imagen final
    }


    private fun simulateScrolling(images: List<String>, finalImageUrl: String?) {
        val handler = Handler(Looper.getMainLooper())
        var currentItem = 0
        val runnable = object : Runnable {
            override fun run() {
                if (currentItem < images.size) {
                    viewPager.setCurrentItem(currentItem++, true)
                    handler.postDelayed(this, 200) // Cambia la imagen cada 200 ms
                }
            }
        }
        handler.post(runnable)
    }




}

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genre_ids: List<Int>,
    val imageUrl: String // Add this assuming you have an image URL
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

