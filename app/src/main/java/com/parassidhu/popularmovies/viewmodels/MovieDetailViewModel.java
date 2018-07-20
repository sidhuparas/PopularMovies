package com.parassidhu.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.parassidhu.popularmovies.database.MovieRepository;
import com.parassidhu.popularmovies.models.CastItem;
import com.parassidhu.popularmovies.models.FavoriteMovie;

import java.util.List;

public class MovieDetailViewModel extends AndroidViewModel {

    private MovieRepository mRepository;

    private LiveData<List<CastItem>> casts;

    public MovieDetailViewModel(@NonNull Application application, String id) {
        super(application);
        mRepository = new MovieRepository(application);

        if (casts == null)
            casts = mRepository.getCast(id);

    }

    public void insertFavMovie(FavoriteMovie movie) {
        mRepository.insertFavMovie(movie);
    }

    public void deleteFavMovie(FavoriteMovie movie) {
        mRepository.deleteFavMovie(movie);
    }

    public LiveData<Integer> isFavorite(int id) {
        return mRepository.isFavorite(id);
    }

    public LiveData<List<CastItem>> getCast() {
        return casts;
    }

}
