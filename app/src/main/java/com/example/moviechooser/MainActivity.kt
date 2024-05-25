    package com.example.moviechooser

    import android.os.Bundle
    import android.widget.Button
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.appcompat.app.AlertDialog
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
        private lateinit var markWatchedButton: Button
        private lateinit var movieTextView: TextView
        private lateinit var movieImageView: ImageView
        private lateinit var selectGenreButton: Button
        private var selectedGenre: String = ""
        private var currentMovieId: Int = -1
        private val genreMap = mapOf(
            "All" to "",
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

        private val watchedMovies: MutableSet<String> by lazy {
            getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).getStringSet("watchedMovies", mutableSetOf()) ?: mutableSetOf()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            fetchButton = findViewById(R.id.button_fetch)
            selectGenreButton = findViewById(R.id.button_select_genre)
            markWatchedButton = findViewById(R.id.button_mark_watched)
            movieTextView = findViewById(R.id.textView_movie)
            movieImageView = findViewById(R.id.imageView_movie)

            selectGenreButton.setOnClickListener {
                showGenreDialog()
            }

            fetchButton.setOnClickListener {
                fetchRandomMovie()
            }

            markWatchedButton.setOnClickListener {
                markMovieAsWatched()
            }
        }

        private fun showGenreDialog() {
            val genres = genreMap.keys.toTypedArray()
            val genreIds = genreMap.values.toTypedArray()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose a Genre")
            builder.setItems(genres) { _, which ->
                selectedGenre = genreIds[which]
                movieTextView.text = "Genre selected: ${genres[which]}"
            }
            builder.create().show()
        }

        private fun markMovieAsWatched() {
            if (currentMovieId != -1) {
                watchedMovies.add(currentMovieId.toString())
                getSharedPreferences("MovieChooserPrefs", MODE_PRIVATE).edit().putStringSet("watchedMovies", watchedMovies).apply()
                movieTextView.text = "Movie marked as watched."
            }
        }

        private fun fetchRandomMovie() {
            val api = RetrofitClient.instance
            api.getMovies("4c442d6c9d9f9e2d444029fbb1fd7732", "es-ES", "popularity.desc", false, false, selectedGenre)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                        response.body()?.let {
                            val movies = it.results.filter { movie -> !watchedMovies.contains(movie.id.toString()) }
                            if (movies.isNotEmpty()) {
                                val randomMovie = movies.random()
                                currentMovieId = randomMovie.id
                                movieTextView.text = randomMovie.title
                                fetchMovieImages(randomMovie.id)
                            } else {
                                movieTextView.text = "No movies found or all watched"
                            }
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
