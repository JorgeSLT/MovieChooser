data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genre_ids: List<Int>
)

data class MovieResponse(
    val results: List<Movie>
)
