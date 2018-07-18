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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieItem> movie);

    @Delete
    void deleteMovie(MovieItem movie);

    @Query("SELECT * FROM movies")
    LiveData<List<MovieItem>> getMovies();

    @Query("SELECT * FROM favorites")
    List<MovieItem> getListOfMovies();

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoriteMovie>> getFavoriteMovies();

    @Insert
    void insertFavoriteMovie(FavoriteMovie movie);

    @Query("SELECT * FROM movies WHERE id=:id")
    MovieItem getMovieById(int id);
}
