package com.parassidhu.popularmovies.activities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.parassidhu.popularmovies.database.MovieDatabase;
import com.parassidhu.popularmovies.database.MovieRepository;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieRepository mRepository;
    private LiveData<List<MovieItem>> allMovies;
    private MovieDatabase mDb;
    private String TAG = getClass().getSimpleName();
    private Application application;
    private MutableLiveData<List<MovieItem>> movies = new MutableLiveData<>();

    private String recentSortBy = Constants.POPULAR_LIST;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
        mDb = MovieDatabase.getDatabase(application);
        this.application = application;

       // allMovies = mRepository.getAllMovies();

        if (mRepository.isOnline()) {
            //insertMovies(new ArrayList<MovieItem>());
            fetchMovies(Constants.FIRST_TIME_URL, recentSortBy);
        } else {
            //allMovies.postValue(new ArrayList<MovieItem>(mRepository.getAllMovies()));
            //insertMovies(movies.getValue());
            //List<MovieItem> list = mRepository.getAllMovies().getValue();
            allMovies = mRepository.getAllMovies();
        }
    }

    public LiveData<List<MovieItem>> getAllMovies() {
        if (allMovies!=null)
        return allMovies;
        else return movies;
    }

    public void insertMovies(List<MovieItem> movies) {
        mRepository.insertMovies(movies);
    }

    public void fetchMovies(String URL, String sortBy) {
        movies =(mRepository.fetchMovies(URL, sortBy));
    }

    public MovieItem getMovieById(int id) {
        return mRepository.getMovieById(id);
    }
}
