package com.example.moviechooser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovieRating::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieRatingDao(): MovieRatingDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_chooser_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
