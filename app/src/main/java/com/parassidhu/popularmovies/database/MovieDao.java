package com.parassidhu.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.parassidhu.popularmovies.models.MovieItem;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieItem> movie);

    @Delete
    void deleteMovie(MovieItem movie);

    @Update
    void updateMovie(MovieItem movie);

    @Query("SELECT * FROM movies")
    LiveData<List<MovieItem>> getMovies();

    @Query("SELECT * FROM movies WHERE id=:id")
    MovieItem getMovieById(int id);
}
