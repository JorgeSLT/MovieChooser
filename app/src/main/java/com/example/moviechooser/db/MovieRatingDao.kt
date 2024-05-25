package com.example.moviechooser.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(movieRating: MovieRating)

    @Query("SELECT rating FROM movies WHERE movieId = :movieId")
    fun getRatingById(movieId: Int): Int?

}
