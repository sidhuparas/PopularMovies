package com.parassidhu.popularmovies.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

import com.parassidhu.popularmovies.models.FavoriteMovie
import com.parassidhu.popularmovies.models.MovieItem

@Database(entities = [MovieItem::class, FavoriteMovie::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {

        private var INSTANCE: MovieDatabase? = null

        private const val DATABASE_NAME = "movie_database"

        fun getDatabase(context: Context): MovieDatabase? {
            if (INSTANCE == null) {
                synchronized(MovieDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                MovieDatabase::class.java, DATABASE_NAME)
                                .build()
                    }
                }
            }

            return INSTANCE
        }
    }
}
