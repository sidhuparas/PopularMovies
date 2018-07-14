package com.parassidhu.popularmovies.activities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.parassidhu.popularmovies.database.MovieRepository;
import com.parassidhu.popularmovies.models.MovieItem;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieRepository mRepository;
    private LiveData<List<MovieItem>> allMovies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
        allMovies = mRepository.getAllMovies();
    }

    public LiveData<List<MovieItem>> getAllMovies() { return allMovies; }

    public void insertMovies(List<MovieItem> movies){ mRepository.insertMovies(movies);}

    public MovieItem getMovieById(int id) { return mRepository.getMovieById(id); }

    public void update(MovieItem movie) { mRepository.update(movie); }
}
