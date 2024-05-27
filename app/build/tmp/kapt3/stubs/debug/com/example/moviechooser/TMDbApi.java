package com.example.moviechooser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\'J\"\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\'J^\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\r\u001a\u00020\b2\b\b\u0001\u0010\u000e\u001a\u00020\b2\b\b\u0001\u0010\u000f\u001a\u00020\u00102\b\b\u0001\u0010\u0011\u001a\u00020\u00102\b\b\u0001\u0010\u0012\u001a\u00020\b2\b\b\u0001\u0010\u0013\u001a\u00020\b2\b\b\u0001\u0010\u0014\u001a\u00020\u0006H\'\u00a8\u0006\u0015"}, d2 = {"Lcom/example/moviechooser/TMDbApi;", "", "getMovieByID", "Lretrofit2/Call;", "Lcom/example/moviechooser/Movie;", "movieId", "", "apiKey", "", "getMovieImages", "Lcom/example/moviechooser/MovieImagesResponse;", "getMovies", "Lcom/example/moviechooser/MovieResponse;", "language", "sortBy", "includeAdult", "", "includeVideo", "genres", "voteAverageGte", "year", "app_debug"})
public abstract interface TMDbApi {
    
    @retrofit2.http.GET(value = "discover/movie")
    @org.jetbrains.annotations.NotNull
    public abstract retrofit2.Call<com.example.moviechooser.MovieResponse> getMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull
    java.lang.String apiKey, @retrofit2.http.Query(value = "language")
    @org.jetbrains.annotations.NotNull
    java.lang.String language, @retrofit2.http.Query(value = "sort_by")
    @org.jetbrains.annotations.NotNull
    java.lang.String sortBy, @retrofit2.http.Query(value = "include_adult")
    boolean includeAdult, @retrofit2.http.Query(value = "include_video")
    boolean includeVideo, @retrofit2.http.Query(value = "with_genres")
    @org.jetbrains.annotations.NotNull
    java.lang.String genres, @retrofit2.http.Query(value = "vote_average.gte")
    @org.jetbrains.annotations.NotNull
    java.lang.String voteAverageGte, @retrofit2.http.Query(value = "year")
    int year);
    
    @retrofit2.http.GET(value = "movie/{movie_id}")
    @org.jetbrains.annotations.NotNull
    public abstract retrofit2.Call<com.example.moviechooser.Movie> getMovieByID(@retrofit2.http.Path(value = "movie_id")
    int movieId, @retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull
    java.lang.String apiKey);
    
    @retrofit2.http.GET(value = "movie/{movie_id}/images")
    @org.jetbrains.annotations.NotNull
    public abstract retrofit2.Call<com.example.moviechooser.MovieImagesResponse> getMovieImages(@retrofit2.http.Path(value = "movie_id")
    int movieId, @retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull
    java.lang.String apiKey);
}