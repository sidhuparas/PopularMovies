package com.parassidhu.popularmovies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.parassidhu.popularmovies.activities.MainActivity;
import com.parassidhu.popularmovies.activities.MovieViewModel;
import com.parassidhu.popularmovies.adapters.MoviesAdapter;
import com.parassidhu.popularmovies.models.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<MovieItem>> allMovies;
    private Application application;

    public MovieRepository(Application application){
        MovieDatabase db = MovieDatabase.getDatabase(application);
        movieDao = db.movieDao();
        allMovies = movieDao.getMovies();
        this.application = application;
    }

    public LiveData<List<MovieItem>> getAllMovies() { return allMovies; }

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
}
