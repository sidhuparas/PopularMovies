package com.parassidhu.popularmovies.viewmodels

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class MovieDetailViewModelFactory(
        private val application: Application,
        private val id: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieDetailViewModel(application, id) as T
    }
}
