package com.parassidhu.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.parassidhu.popularmovies.database.MovieDatabase;
import com.parassidhu.popularmovies.database.MovieRepository;
import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieRepository mRepository;
    private LiveData<List<MovieItem>> allMovies = new MutableLiveData<>();
    private MovieDatabase mDb;
    private String TAG = getClass().getSimpleName();
    private Application application;

    private String recentSortBy = Constants.POPULAR_LIST;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
        mDb = MovieDatabase.getDatabase(application);
        this.application = application;

        decideNetworkRequestOrExisting();
    }

    public void decideNetworkRequestOrExisting() {
        if (mRepository.isOnline()) {
            fetchMovies(Constants.FIRST_TIME_URL, recentSortBy);
        } else {
            allMovies = mRepository.getAllMovies();
        }
    }

    public LiveData<List<MovieItem>> getAllMovies() {
        Log.d(TAG, "getAllMovies: Added");
        //decideNetworkRequestOrExisting();
        return allMovies;
    }

    public LiveData<List<MovieItem>> getOfflineMovies(){
        return mRepository.getAllMovies();
    }

    public void fetchMovies(String URL, String sortBy) {
        recentSortBy = sortBy;
        allMovies = mRepository.fetchMovies(URL, sortBy);
    }

    public void insertFavMovie(FavoriteMovie movie) {
        mRepository.insertFavMovie(movie);
    }

    public void deleteFavMovie(FavoriteMovie movie) { mRepository.deleteFavMovie(movie); }

    public LiveData<Integer> isFavorite(int id) { return mRepository.isFavorite(id); }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return mRepository.getFavoriteMovies();
    }
}
