package com.parassidhu.popularmovies.utils;

import com.parassidhu.popularmovies.BuildConfig;

public class Constants {

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String POPULAR_LIST = BASE_URL + "movie/popular?api_key=" + BuildConfig.API_KEY;
    public static final String TOP_RATED_LIST = BASE_URL + "movie/top_rated?api_key=" + BuildConfig.API_KEY;
    public static final String BASE_IMAGE = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_BACKDROP = "http://image.tmdb.org/t/p/w780";


    public static final String B_BACKDROP = "backdrop";
    public static final String B_POSTER = "poster";
    public static final String B_TITLE = "title";
    public static final String B_RELEASE_DATE = "release_date";
    public static final String B_VOTE_AVERAGE = "vote_average";
    public static final String B_OVERVIEW = "overview";

}
