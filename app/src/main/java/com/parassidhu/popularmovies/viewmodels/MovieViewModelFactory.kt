package com.parassidhu.popularmovies.viewmodels

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import com.parassidhu.popularmovies.database.MovieDatabase

class MovieViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieViewModel(application) as T
    }
}
