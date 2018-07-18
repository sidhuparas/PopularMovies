package com.parassidhu.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;

import java.util.List;

@Dao
public interface MovieDao {

    // Movies Table

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieItem> movie);

    @Query("SELECT * FROM movies")
    LiveData<List<MovieItem>> getMovies();


    // FavoritesTable

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoriteMovie>> getFavoriteMovies();

    @Query("SELECT COUNT(id) FROM favorites WHERE id = :id")
    LiveData<Integer> isFavorite(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMovie(FavoriteMovie movie);

    @Delete
    void deleteFavoriteMovie(FavoriteMovie movie);

}
