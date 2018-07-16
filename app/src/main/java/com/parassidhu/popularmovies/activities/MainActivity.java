package com.parassidhu.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.parassidhu.popularmovies.BuildConfig;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.adapters.MoviesAdapter;
import com.parassidhu.popularmovies.database.MovieDatabase;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.parassidhu.popularmovies.utils.ItemClickSupport;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.moviesList) RecyclerView moviesList;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.popular) Chip popular;
    @BindView(R.id.top_rated) Chip top_rated;

    private ArrayList<MovieItem> moviesItems = new ArrayList<>();
    private GridLayoutManager mLayoutManager;
    private MoviesAdapter adapter;
    private int pageNum;
    private String sortBy;  // Stores user-selected URL, Popular Movies or Top-Rated

    private MovieViewModel mViewModel;

    private String TAG = getClass().getSimpleName();
    public static final String MOVIE_KEY = "movie_item";

    private Comparator<MovieItem> comparator = new Comparator<MovieItem>() {
        @Override
        public int compare(MovieItem movieItem, MovieItem t1) {
            Double d = Double.valueOf(movieItem.getPopularity())
                    - Double.valueOf(t1.getPopularity());
            if (d > 0) return -1;
            else if (d == 0) return 0;
            else return 1;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        sortBy = Constants.POPULAR_LIST;
        setupViewModel();
        // mViewModel.fetchMovies(getURL(pageNum));
        popular.setSelected(true);
    }

    // Basic setup of views
    private void init() {
        Stetho.initializeWithDefaults(this);

        // Setup RecyclerView
        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3;
        }

        pageNum = 1;

        mLayoutManager = new GridLayoutManager(this, spanCount);
        moviesList.setLayoutManager(mLayoutManager);

        // Setup Custom Toolbar
        setSupportActionBar(toolbar);
        setTitle("");

        // Show progress bar and hide list
        showProgressBar(true);

        //Set Listeners
        setChipListeners();
        setMovieClickListener();
        addListScrollListener();
    }

    // Handle ViewModel
    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        mViewModel.getAllMovies().observe(this, new Observer<List<MovieItem>>() {
            @Override
            public void onChanged(@Nullable List<MovieItem> movieItems) {
                if (movieItems!=null) {
                    Log.d(TAG, "onChanged: " + movieItems.size());
                    moviesItems.clear();
                    moviesItems.addAll(movieItems);
                    if (movieItems != null) {
                        showInUI(movieItems);
                    }
                    showProgressBar(false);
                }else {
                    Log.d(TAG, "onChanged: It's null");
                }
            }
        });
    }

    private void showInUI(List<MovieItem> movieItems) {
        Log.d(TAG, "onChanged: Notified: " + movieItems.size());
        if (moviesList.getAdapter() == null) {
            adapter = new MoviesAdapter(MainActivity.this, moviesItems);
            moviesList.setAdapter(adapter);
        } else {
            Log.d(TAG, "showInUI: DataSetNotified");
            adapter.notifyDataSetChanged();
        }
    }

    private void addListScrollListener() {
        moviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Total Items
                int count = mLayoutManager.getItemCount();

                if ((dy > 0) && (count / 20 == pageNum)) {
                    int holderCount = mLayoutManager.getChildCount();
                    int oldCount = mLayoutManager.findFirstVisibleItemPosition();

                    // Checks if user is on the verge of end of the list
                    if (holderCount + oldCount >= (count - 10)) {
                        // Increments the page number and fetch the result
                        pageNum++;
                        mViewModel.fetchMovies(getURL(pageNum),sortBy);
                    }
                }
            }
        });
    }

    private String getURL(int page) {
        Uri uri = Uri.parse(sortBy).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .appendQueryParameter("page", String.valueOf(page))
                .build();

        return uri.toString();
    }

    private void showError() {
        Toast.makeText(MainActivity.this,
                "Please retry after some time!", Toast.LENGTH_SHORT).show();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Sets listener for individual movie click
    private void setMovieClickListener() {
        ItemClickSupport.addTo(moviesList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                Bundle bundle = new Bundle();

                MovieItem item = moviesItems.get(position);

                bundle.putParcelable(MOVIE_KEY, item);
                intent.putExtra("Values", bundle);

                // Shared Element Transition
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, v, getResources().getString(R.string.img_trans));

                startActivity(intent, options.toBundle());
            }
        });
    }

    // Set listener for click of "Popular" or "Top Rated"
    private void setChipListeners() {
        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlChip(true, false);
                sortBy = Constants.POPULAR_LIST;
                reset();
                mViewModel.fetchMovies(getURL(pageNum), sortBy);
            }
        });

        top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlChip(false, true);
                sortBy = Constants.TOP_RATED_LIST;
                reset();
                mViewModel.fetchMovies(getURL(pageNum), sortBy);
            }
        });
    }

    // Executes when user switches between Pop,Top_Rated or Favs
    private void reset(){
        moviesItems.clear();
        pageNum = 1;
        moviesList.setAdapter(null);
        showProgressBar(true);
    }

    // Helper method
    private void showProgressBar(boolean progress) {
        if (progress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    // Helper method
    private void controlChip(boolean pop, boolean toprated) {
        if (pop) {
            popular.setSelected(true);
        } else {
            popular.setSelected(false);
        }

        if (toprated) {
            top_rated.setSelected(true);
        } else {
            top_rated.setSelected(false);
        }
    }

}
