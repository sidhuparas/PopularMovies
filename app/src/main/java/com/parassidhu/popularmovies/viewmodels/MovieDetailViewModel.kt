package com.parassidhu.popularmovies.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import com.parassidhu.popularmovies.database.MovieRepository
import com.parassidhu.popularmovies.models.CastItem
import com.parassidhu.popularmovies.models.FavoriteMovie
import com.parassidhu.popularmovies.models.ReviewItem
import com.parassidhu.popularmovies.models.TrailerItem

class MovieDetailViewModel internal constructor(application: Application, id: String) : AndroidViewModel(application) {

    private val mRepository: MovieRepository = MovieRepository(application)

    var cast: LiveData<List<CastItem>>? = null
        private set
    var trailers: LiveData<List<TrailerItem>>? = null
        private set
    var reviews: LiveData<List<ReviewItem>>? = null
        private set


    init {
        if (cast == null)
            cast = mRepository.getCast(id)

        if (trailers == null)
            trailers = mRepository.getTrailers(id)

        if (reviews == null)
            reviews = mRepository.getReviews(id)
    }

    fun insertFavMovie(movie: FavoriteMovie) {
        mRepository.insertFavMovie(movie)
    }

    fun deleteFavMovie(movie: FavoriteMovie) {
        mRepository.deleteFavMovie(movie)
    }

    fun isFavorite(id: Int): LiveData<Int> {
        return mRepository.isFavorite(id)
    }
}
