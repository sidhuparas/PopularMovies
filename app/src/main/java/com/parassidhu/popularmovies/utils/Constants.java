package com.parassidhu.popularmovies.utils;

import com.parassidhu.popularmovies.BuildConfig;

public class Constants {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String POPULAR_LIST = BASE_URL + "movie/popular?api_key=" + BuildConfig.API_KEY;
    public static final String TOP_RATED_LIST = BASE_URL + "movie/top_rated?api_key=" + BuildConfig.API_KEY;
    public static final String BASE_IMAGE = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_BACKDROP = "http://image.tmdb.org/t/p/w780";

}
