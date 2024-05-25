package com.example.moviechooser.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(movieRating: MovieRating)

    @Query("SELECT * FROM movie_ratings WHERE movieId = :movieId")
    suspend fun getRatingByMovieId(movieId: Int): MovieRating?
}
