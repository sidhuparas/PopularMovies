package com.parassidhu.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.parassidhu.popularmovies.database.MovieRepository;
import com.parassidhu.popularmovies.models.FavoriteMovie;

import java.util.List;

public class MovieDetailViewModel extends AndroidViewModel{

    private MovieRepository mRepository;
    private LiveData<List<FavoriteMovie>> favMovies = new MutableLiveData<>();

    public MovieDetailViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
    }


}
