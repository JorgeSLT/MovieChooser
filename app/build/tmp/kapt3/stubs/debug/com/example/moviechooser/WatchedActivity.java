package com.example.moviechooser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tH\u0002J\u0010\u0010\r\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0004H\u0002J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tH\u0002J\u000e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u0012\u0010\u0010\u001a\u00020\u000b2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0014J\b\u0010\u0013\u001a\u00020\u000bH\u0002J\b\u0010\u0014\u001a\u00020\u000bH\u0002J\b\u0010\u0015\u001a\u00020\u000bH\u0002J\u0018\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/example/moviechooser/WatchedActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "currentSetPos", "", "db", "Lcom/example/moviechooser/db/AppDatabase;", "watchedMovieIds", "", "", "fetchMovieDetailsById", "", "movieId", "fetchMovieImages", "fetchMovieRating", "loadWatchedMovieIds", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "shareMovieDetails", "showRatingDialog", "updateMovieDetails", "updateMovieRating", "rating", "app_debug"})
public final class WatchedActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.moviechooser.db.AppDatabase db;
    private int currentSetPos = -1;
    private java.util.List<java.lang.String> watchedMovieIds;
    
    public WatchedActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void showRatingDialog() {
    }
    
    private final void shareMovieDetails() {
    }
    
    private final void updateMovieRating(java.lang.String movieId, int rating) {
    }
    
    private final void fetchMovieRating(java.lang.String movieId) {
    }
    
    private final void updateMovieDetails() {
    }
    
    private final java.util.List<java.lang.String> loadWatchedMovieIds() {
        return null;
    }
    
    private final void fetchMovieDetailsById(java.lang.String movieId) {
    }
    
    private final void fetchMovieImages(int movieId) {
    }
}