package com.example.moviechooser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\tH\u0002J\u0010\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0004H\u0002J\u000e\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\t0\u000fH\u0002J\b\u0010\u0018\u001a\u00020\u0014H\u0002J\u0012\u0010\u0019\u001a\u00020\u00142\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0014J\b\u0010\u001c\u001a\u00020\u0014H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R!\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R!\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\r\u001a\u0004\b\u0011\u0010\u000b\u00a8\u0006\u001d"}, d2 = {"Lcom/example/moviechooser/WatchlistActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "currentSetPos", "", "db", "Lcom/example/moviechooser/db/AppDatabase;", "watchedMovies", "", "", "getWatchedMovies", "()Ljava/util/Set;", "watchedMovies$delegate", "Lkotlin/Lazy;", "watchlistMovieIds", "", "watchlistMovies", "getWatchlistMovies", "watchlistMovies$delegate", "fetchMovieDetailsById", "", "movieId", "fetchMovieImages", "loadWatchlistMovieIds", "markMovieAsWatched", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "updateMovieDetails", "app_debug"})
public final class WatchlistActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.moviechooser.db.AppDatabase db;
    private int currentSetPos = -1;
    private java.util.List<java.lang.String> watchlistMovieIds;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy watchedMovies$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy watchlistMovies$delegate = null;
    
    public WatchlistActivity() {
        super();
    }
    
    private final java.util.Set<java.lang.String> getWatchedMovies() {
        return null;
    }
    
    private final java.util.Set<java.lang.String> getWatchlistMovies() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void markMovieAsWatched() {
    }
    
    private final void updateMovieDetails() {
    }
    
    private final java.util.List<java.lang.String> loadWatchlistMovieIds() {
        return null;
    }
    
    private final void fetchMovieDetailsById(java.lang.String movieId) {
    }
    
    private final void fetchMovieImages(int movieId) {
    }
}