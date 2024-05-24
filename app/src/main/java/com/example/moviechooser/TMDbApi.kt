import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

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
