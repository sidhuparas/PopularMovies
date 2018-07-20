package com.parassidhu.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private final Application application;
    private String id;

    public MovieDetailViewModelFactory(Application application, String id) {
        this.application = application;
        this.id = id;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new MovieDetailViewModel(application, id);
    }
}
