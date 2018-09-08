package com.parassidhu.popularmovies.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log

import com.parassidhu.popularmovies.database.MovieDatabase
import com.parassidhu.popularmovies.database.MovieRepository
import com.parassidhu.popularmovies.models.FavoriteMovie
import com.parassidhu.popularmovies.models.MovieItem
import com.parassidhu.popularmovies.utils.Constants

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: MovieRepository = MovieRepository(application)
    private var allMovies: LiveData<List<MovieItem>> = MutableLiveData()
    private val mDb: MovieDatabase? = MovieDatabase.getDatabase(application)
    private val TAG = javaClass.simpleName

    private var recentSortBy = Constants.POPULAR_LIST

    private val offlineMovies: LiveData<List<MovieItem>>
        get() = mRepository.getAllMovies(recentSortBy)

    val favoriteMovies: LiveData<List<FavoriteMovie>>
        get() = mRepository.favoriteMovies

    init {
        decideNetworkRequestOrExisting(Constants.FIRST_TIME_URL)
    }

    private fun decideNetworkRequestOrExisting(URL: String): LiveData<List<MovieItem>> {
        return if (mRepository.isOnline) {
            allMovies = MutableLiveData()
            fetchMovies(URL, recentSortBy)
        } else {
            offlineMovies
        }
    }

    fun getAllMovies(URL: String, sort_by: String): LiveData<List<MovieItem>> {
        recentSortBy = sort_by
        return decideNetworkRequestOrExisting(URL)
    }

    fun fetchMovies(URL: String, sortBy: String): LiveData<List<MovieItem>> {
        recentSortBy = sortBy

        val list = mRepository.fetchMovies(URL, sortBy)
        allMovies = list
        return list
    }
}
