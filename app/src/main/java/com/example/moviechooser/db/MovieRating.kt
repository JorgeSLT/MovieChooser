package com.example.moviechooser.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieRating(
    @PrimaryKey val movieId: Int,
    var rating: Int?
)
