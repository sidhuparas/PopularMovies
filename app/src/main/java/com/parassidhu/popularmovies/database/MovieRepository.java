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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.parassidhu.popularmovies.BuildConfig;
import com.parassidhu.popularmovies.models.CastItem;
import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.models.ReviewItem;
import com.parassidhu.popularmovies.models.TrailerItem;
import com.parassidhu.popularmovies.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        allMovies = movieDao.getMovies(recentSortBy);
        this.application = application;
    }

    public LiveData<List<MovieItem>> getAllMovies(String sort_by) {
        return movieDao.getMovies(sort_by);
    }

    public void insertFavMovie(FavoriteMovie movie) { new insertFavAsync(movieDao).execute(movie); }

    public void deleteFavMovie(FavoriteMovie movie) { new deleteFavAsync(movieDao).execute(movie); }

    private void insertMovies(List<MovieItem> movies) { new insertAsyncTask(movieDao).execute(movies); }

    public LiveData<Integer> isFavorite(int id) { return movieDao.isFavorite(id); }

    private static class insertAsyncTask extends AsyncTask<List<MovieItem>, Void, Void> {

        private MovieDao mAsyncTaskDao;

        insertAsyncTask(MovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<MovieItem>... params) {
            try {
                mAsyncTaskDao.insertMovies(params[0]);
            }catch (Exception ignored){}
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

    private static class deleteFavAsync extends AsyncTask<FavoriteMovie, Void, Void> {

        private MovieDao mAsyncTaskDao;

        deleteFavAsync(MovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FavoriteMovie... params) {
            mAsyncTaskDao.deleteFavoriteMovie(params[0]);
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
                    item.setSortBy(sortBy);
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


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public LiveData<List<CastItem>> getCast(String id){
        final MutableLiveData<List<CastItem>> result = new MutableLiveData<>();
        String URL = "http://api.themoviedb.org/3/movie/" + id + "/casts?api_key=" + BuildConfig.API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.optJSONArray("cast");

                    if (jsonArray.length()>0) {
                        Gson gson = new Gson();
                        List<CastItem> list = gson.fromJson(jsonArray.toString(),
                                new TypeToken<List<CastItem>>() {
                                }.getType());

                        result.setValue(list);
                    }
                }catch (Exception ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(application);
        requestQueue.add(stringRequest);

        return result;
    }

    public LiveData<List<TrailerItem>> getTrailers(String id){
        final MutableLiveData<List<TrailerItem>> result = new MutableLiveData<>();
        String URL = "http://api.themoviedb.org/3/movie/" + id + "/trailers?api_key=" + BuildConfig.API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.optJSONArray("youtube");

                    if (jsonArray.length()>0) {
                        Gson gson = new Gson();
                        List<TrailerItem> list = gson.fromJson(jsonArray.toString(),
                                new TypeToken<List<TrailerItem>>() {
                                }.getType());

                        result.setValue(list);
                    }
                }catch (Exception ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(application);
        requestQueue.add(stringRequest);

        return result;
    }

    public LiveData<List<ReviewItem>> getReviews(String id){
        final MutableLiveData<List<ReviewItem>> result = new MutableLiveData<>();
        String URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + BuildConfig.API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.optJSONArray("results");

                    if (jsonArray.length()>0) {
                        Gson gson = new Gson();
                        List<ReviewItem> list = gson.fromJson(jsonArray.toString(),
                                new TypeToken<List<ReviewItem>>() {
                                }.getType());

                        result.setValue(list);
                    }
                }catch (Exception ignored){ }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(application);
        requestQueue.add(stringRequest);

        return result;
    }
}
