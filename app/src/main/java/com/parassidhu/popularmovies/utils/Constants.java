package com.parassidhu.popularmovies.utils;

import com.parassidhu.popularmovies.BuildConfig;

public class Constants {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String POPULAR_LIST = BASE_URL + "movie/popular";
    public static final String TOP_RATED_LIST = BASE_URL + "movie/top_rated";
    public static final String BASE_IMAGE = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_BACKDROP = "http://image.tmdb.org/t/p/w780";
    public static final String FIRST_TIME_URL = POPULAR_LIST + "?api_key=" + BuildConfig.API_KEY;
    public static final String FAVORITES_KEY = "favs";

    public static final String MOVIE_BASE = "http://api.themoviedb.org/3/movie/";
    public static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";
}
