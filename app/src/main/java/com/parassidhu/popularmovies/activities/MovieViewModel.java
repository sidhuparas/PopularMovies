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
    private ArrayList<MovieItem> moviesItems = new ArrayList<>();
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


        if (isOnline()) {
            insertMovies(new ArrayList<MovieItem>());
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

    public MovieItem getMovieById(int id) {
        return mRepository.getMovieById(id);
    }

    // Fetch JSON from the API
    public void fetchMovies(String URL, final String sortBy) {
        /*if (pageNum == 1) {
            controlViews(true, false);
        }*/

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseAndShowInUi(response, sortBy);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               /* controlViews(false, false);
                showError();*/
            }
        });

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Parse the response and show to the main user interface
    private void parseAndShowInUi(String response, String sortBy) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.optJSONArray("results");

            if (jsonArray.length() > 0) {
                Gson gson = new Gson();

                // Creates a new ArrayList of fetched result
                ArrayList<MovieItem> items = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    MovieItem item = gson.fromJson(jsonArray.optJSONObject(i).toString(),
                            MovieItem.class);
                    //item.setSortBy(sortBy);
                    items.add(item);
                }
                // Adds the above ArrayList to main ArrayList which is
                // to be passed

                if (!recentSortBy.equals(sortBy)) {
                    moviesItems.clear();
                    recentSortBy = sortBy;
                }

                moviesItems.addAll(items);

                insertMovies(moviesItems);
                movies.postValue(moviesItems);
                Log.d(TAG, "parseAndShowInUi: Done");

            }
        } catch (JSONException e) {
            /*Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            showError();
            controlViews(false, false);*/
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
