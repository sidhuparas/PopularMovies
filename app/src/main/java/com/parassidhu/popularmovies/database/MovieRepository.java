package com.parassidhu.popularmovies.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<MovieItem>> allMovies;
    private Application application;
    private String TAG = this.getClass().getSimpleName();
    private ArrayList<MovieItem> moviesItems = new ArrayList<>();

    private String recentSortBy = Constants.POPULAR_LIST;
    private MutableLiveData<List<MovieItem>> result = new MutableLiveData<>();

    public MovieRepository(Application application){
        MovieDatabase db = MovieDatabase.getDatabase(application);
        movieDao = db.movieDao();
        allMovies = movieDao.getMovies();
        this.application = application;
    }

    public LiveData<List<MovieItem>> getAllMovies() { return allMovies; }

    public void insertFavMovie(FavoriteMovie movie) { new insertFavAsync(movieDao).execute(movie); }

    public void insertMovies(List<MovieItem> movies) { new insertAsyncTask(movieDao).execute(movies); }

    public MovieItem getMovieById(int id) { return movieDao.getMovieById(id); }

    private static class insertAsyncTask extends AsyncTask<List<MovieItem>, Void, Void> {

        private MovieDao mAsyncTaskDao;

        insertAsyncTask(MovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<MovieItem>... params) {
            mAsyncTaskDao.insertMovies(params[0]);
            return null;
        }
    }

    private static class insertFavAsync extends AsyncTask<FavoriteMovie, Void, Void> {

        private MovieDao mAsyncTaskDao;

        insertFavAsync(MovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FavoriteMovie... params) {
            mAsyncTaskDao.insertFavoriteMovie(params[0]);
            return null;
        }
    }

    // Fetch JSON from the API
    public MutableLiveData<List<MovieItem>> fetchMovies(String URL, final String sortBy) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result.setValue(parseAndShowInUi(response, sortBy));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //result.setValue(movieDao.getMovies().getValue());
                result.setValue(new ArrayList<MovieItem>());
                Log.d(TAG, "onErrorResponse: Failed");
            }
        });

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        requestQueue.add(stringRequest);

        return result;
    }

    // Parse the response and show to the main user interface
    private List<MovieItem> parseAndShowInUi(String response, String sortBy) {
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
                Log.d(TAG, "loadFromNetwork: Done");
                return moviesItems;

            }
        } catch (JSONException e) { return null;}
        return null;
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies(){
        return movieDao.getFavoriteMovies();
    }

    public List<MovieItem> getListOfMovies(){
        return movieDao.getListOfMovies();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
