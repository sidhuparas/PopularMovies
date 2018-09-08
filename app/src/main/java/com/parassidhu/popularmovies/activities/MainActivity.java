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
import android.support.constraint.ConstraintLayout;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parassidhu.popularmovies.BuildConfig;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.adapters.MoviesAdapter;
import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.parassidhu.popularmovies.utils.ItemClickSupport;
import com.parassidhu.popularmovies.viewmodels.MovieViewModel;
import com.parassidhu.popularmovies.viewmodels.MovieViewModelFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.moviesList) RecyclerView moviesList;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.popular) Chip popular;
    @BindView(R.id.top_rated) Chip top_rated;
    @BindView(R.id.favorites_chip) Chip favorites;
    @BindView(R.id.network_retry_lay) ConstraintLayout layout;

    private ArrayList<MovieItem> moviesItems = new ArrayList<>();
    private GridLayoutManager mLayoutManager;
    private MoviesAdapter adapter;
    private int pageNum;
    private static String sortBy = Constants.POPULAR_LIST;  // Stores user-selected URL, Popular Movies or Top-Rated

    private MovieViewModel mViewModel;

    private String TAG = getClass().getSimpleName();
    public static final String MOVIE_KEY = "movie_item";

    private Comparator<MovieItem> compareByPopularity = new Comparator<MovieItem>() {
        @Override
        public int compare(MovieItem movieItem, MovieItem t1) {
            Double d = Double.valueOf(movieItem.getPopularity())
                    - Double.valueOf(t1.getPopularity());
            if (d > 0) return -1;
            else if (d == 0) return 0;
            else return 1;
        }
    };

    private Comparator<MovieItem> compareByRating = new Comparator<MovieItem>() {
        @Override
        public int compare(MovieItem movieItem, MovieItem t1) {
            Double d = Double.valueOf(movieItem.getVoteAverage())
                    - Double.valueOf(t1.getVoteAverage());
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

        if (savedInstanceState == null) {
            popular.setSelected(true);
        } else {
            determineChipToSelect(savedInstanceState);
        }

        setupViewModel();
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
        mViewModel = ViewModelProviders.of(this, new MovieViewModelFactory
                (this.getApplication())).get(MovieViewModel.class);

        if (!isFavoritesSelected()) {
            mViewModel.getAllMovies(getURL(pageNum), sortBy).observe(this, allMoviesObserver);
        }
    }

    Observer<List<MovieItem>> allMoviesObserver = new Observer<List<MovieItem>>() {
        @Override
        public void onChanged(@Nullable List<MovieItem> movieItems) {
            if (movieItems != null) {
                if (movieItems.size() != 0) {
                    moviesItems.clear();
                    sortOfflineMovies(movieItems);

                    Map<Integer, MovieItem> map = removeDuplicateValues(movieItems);

                    setValuesToAdapter(map.values());
                    layout.setVisibility(View.GONE);
                } else {
                    showProgressBar(false);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void sortOfflineMovies(@NonNull List<MovieItem> movieItems) {
        if (!isOnline()) {
            if (sortBy.equals(Constants.POPULAR_LIST))
                Collections.sort(movieItems, compareByPopularity);
            else if(sortBy.equals(Constants.TOP_RATED_LIST))
                Collections.sort(movieItems, compareByRating);
        }
    }

    @NonNull
    private Map<Integer, MovieItem> removeDuplicateValues(@NonNull List<MovieItem> movieItems) {
        Map<Integer, MovieItem> map = new LinkedHashMap<>();

        for (MovieItem item : movieItems) {
            map.put(item.getId(), item);
        }
        return map;
    }

    private void getFavs() {
        mViewModel = ViewModelProviders.of(this, new MovieViewModelFactory
                (this.getApplication())).get(MovieViewModel.class);

        mViewModel.getFavoriteMovies().observe(this, favoriteMoviesObserver);
    }

    Observer<List<FavoriteMovie>> favoriteMoviesObserver = new Observer<List<FavoriteMovie>>() {
        @Override
        public void onChanged(@Nullable List<FavoriteMovie> favoriteMovies) {
          if (isFavoritesSelected()) {
              Gson gson = new Gson();
              String json = gson.toJson(favoriteMovies);
              List<MovieItem> list = gson.fromJson(json, new TypeToken<List<MovieItem>>() {
              }.getType());
              moviesItems.clear();
              setValuesToAdapter(list);
              Log.d(TAG, "Retrieved Favorites");

              if (moviesItems.size()==0){
                  Toast.makeText(MainActivity.this, "No Favorites Found!"
                          , Toast.LENGTH_LONG).show();
              }
          }
        }
    };

    private void setValuesToAdapter(Collection<MovieItem> list2) {
        moviesItems.addAll(list2);
        showInUI();
        showProgressBar(false);
    }


    private void showInUI() {
        if (moviesList.getAdapter() == null) {
            adapter = new MoviesAdapter(MainActivity.this, moviesItems);
            moviesList.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void addListScrollListener() {
        moviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Total Items
                if (!isFavoritesSelected()) {
                    int count = mLayoutManager.getItemCount();

                    if ((dy > 0) && (count / 20 == pageNum)) {
                        int holderCount = mLayoutManager.getChildCount();
                        int oldCount = mLayoutManager.findFirstVisibleItemPosition();

                        // Checks if user is on the verge of end of the list
                        if (holderCount + oldCount >= (count - 10)) {
                            // Increments the page number and fetch the result
                            pageNum++;
                            mViewModel.fetchMovies(getURL(pageNum), sortBy);
                            Log.d(TAG, "onScrolled: " + pageNum);
                        }
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

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Sets listener for individual movie click
    private void setMovieClickListener() {
        ItemClickSupport.Companion.addTo(moviesList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
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
                controlChip(true, false, false);
                sortBy = Constants.POPULAR_LIST;
                reset();
                newChipClick();
            }
        });

        top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlChip(false, true, false);
                sortBy = Constants.TOP_RATED_LIST;
                reset();
                newChipClick();
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlChip(false, false, true);
                sortBy = Constants.FAVORITES_KEY;
                reset();
                getFavs();
                // newChipClick();
            }
        });
    }

    private void newChipClick() {
        if (isOnline()) {
            setupViewModel();
            mViewModel.getAllMovies(getURL(pageNum), sortBy);
        } else {
           mViewModel.getAllMovies(getURL(pageNum), sortBy).removeObserver(allMoviesObserver);
            setupViewModel();
            //mViewModel.decideNetworkRequestOrExisting();
            showProgressBar(false);
        }
       }

    // Executes when user switches between Pop,Top_Rated or Favs
    private void reset() {
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
    private void controlChip(boolean pop, boolean toprated, boolean fav) {
        if (pop) popular.setSelected(true);
        else popular.setSelected(false);

        if (toprated) top_rated.setSelected(true);
        else top_rated.setSelected(false);

        if (fav) favorites.setSelected(true);
        else favorites.setSelected(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (popular.isSelected()) outState.putString(MOVIE_KEY, Constants.POPULAR_LIST);
        else if (top_rated.isSelected()) outState.putString(MOVIE_KEY, Constants.TOP_RATED_LIST);
        else outState.putString(MOVIE_KEY, Constants.FAVORITES_KEY);
    }

    @OnClick(R.id.retry_btn)
    public void retry() {
        layout.setVisibility(View.GONE);
        showProgressBar(true);
        newChipClick();
    }

    // After configuration changes, select a chip
    private void determineChipToSelect(Bundle savedInstanceState) {
        String selectedChip = savedInstanceState.getString(MOVIE_KEY, Constants.POPULAR_LIST);

        switch (selectedChip) {
            case Constants.POPULAR_LIST:
                controlChip(true, false, false);
                break;
            case Constants.TOP_RATED_LIST:
                controlChip(false, true, false);
                break;
            default:
                getFavs();
                moviesItems.clear();
                controlChip(false, false, true);
                break;
        }
    }

    private Boolean isFavoritesSelected() {
        return favorites.isSelected();
    }
}
