package com.parassidhu.popularmovies.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.parassidhu.popularmovies.models.FavoriteMovie
import com.parassidhu.popularmovies.models.MovieItem

@Dao
interface MovieDao {

    // FavoritesTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(movie: List<MovieItem>)

    @get:Query("SELECT * FROM favorites")
    val favoriteMovies: LiveData<List<FavoriteMovie>>

    // Movies Table

    @Query("SELECT * FROM movies WHERE sort_by = :sort_by")
    fun getMovies(sort_by: String): LiveData<List<MovieItem>>

    @Query("SELECT COUNT(id) FROM favorites WHERE id = :id")
    fun isFavorite(id: Int): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteMovie(movie: FavoriteMovie)

    @Delete
    fun deleteFavoriteMovie(movie: FavoriteMovie)

}
