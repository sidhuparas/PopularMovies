package com.parassidhu.popularmovies.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.parassidhu.popularmovies.models.MovieItem;

import java.util.List;

public class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<MovieItem>> allMovies;

    public MovieRepository(Application application){
        MovieDatabase db = MovieDatabase.getDatabase(application);
        movieDao = db.movieDao();
        allMovies = movieDao.getMovies();
    }

    public LiveData<List<MovieItem>> getAllMovies() { return allMovies; }

    public void insert(MovieItem item) {  }
}
