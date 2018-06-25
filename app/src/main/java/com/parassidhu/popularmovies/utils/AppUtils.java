package com.parassidhu.popularmovies.utils;

import com.parassidhu.popularmovies.BuildConfig;

public class AppUtils {

    public static final String BASE_URL = "http://api.themoviedb.org/3";
    public static final String POPULAR_LIST = BASE_URL + "movie/popular?api_key=" + BuildConfig.API_KEY;
}
