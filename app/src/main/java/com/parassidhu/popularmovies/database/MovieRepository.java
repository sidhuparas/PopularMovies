package com.parassidhu.popularmovies.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.parassidhu.popularmovies.models.MovieItem;

import java.util.List;

public class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<MovieItem>> allMovies;

    public MovieRepository(Application application){
        MovieDatabase db = MovieDatabase.getDatabase(application);
        movieDao = db.movieDao();
        allMovies = movieDao.getMovies();
    }

    public LiveData<List<MovieItem>> getAllMovies() { return allMovies; }

    public void insertMovies(List<MovieItem> movies) { new insertAsyncTask(movieDao).execute(movies); }

    public MovieItem getMovieById(int id) { return movieDao.getMovieById(id); }

    public void update(MovieItem movie) { new updateAsyncTask(movieDao).execute(movie); }

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
    private static class updateAsyncTask extends AsyncTask<MovieItem, Void, Void> {

        private MovieDao mAsyncTaskDao;

        updateAsyncTask(MovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MovieItem... params) {
            mAsyncTaskDao.updateMovie(params[0]);
            return null;
        }
    }
}
