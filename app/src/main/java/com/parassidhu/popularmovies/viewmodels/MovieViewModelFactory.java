package com.parassidhu.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.parassidhu.popularmovies.database.MovieDatabase;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Application application;

    public MovieViewModelFactory(Application application) {
        this.application = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new MovieViewModel(application);
    }
}
