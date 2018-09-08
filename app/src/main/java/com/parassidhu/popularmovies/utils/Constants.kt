package com.parassidhu.popularmovies.utils

import com.parassidhu.popularmovies.BuildConfig

object Constants {

    private const val BASE_URL = "http://api.themoviedb.org/3/"
    const val POPULAR_LIST = BASE_URL + "movie/popular"
    const val TOP_RATED_LIST = BASE_URL + "movie/top_rated"
    const val BASE_IMAGE = "http://image.tmdb.org/t/p/w185"
    const val BASE_BACKDROP = "http://image.tmdb.org/t/p/w780"
    const val FIRST_TIME_URL = POPULAR_LIST + "?api_key=" + BuildConfig.API_KEY
    const val FAVORITES_KEY = "favs"

    const val MOVIE_BASE = "http://api.themoviedb.org/3/movie/"
    const val YOUTUBE_BASE = "https://www.youtube.com/watch?v="
}
