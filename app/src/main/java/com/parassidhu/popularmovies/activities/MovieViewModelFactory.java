package com.parassidhu.popularmovies.activities;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.parassidhu.popularmovies.database.MovieDatabase;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MovieDatabase db;
    private final Application application;

    public MovieViewModelFactory(Application application, MovieDatabase db) {
        this.db = db;
        this.application = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new MovieViewModel(application);
    }
}
