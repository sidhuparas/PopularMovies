package com.parassidhu.popularmovies.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parassidhu.popularmovies.BuildConfig;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.adapters.MoviesAdapter;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.parassidhu.popularmovies.utils.ItemClickSupport;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

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
    private String latestList;  // Stores user-selected URL, Popular Movies or Top-Rated

    private String TAG = getClass().getSimpleName();
    public static final String MOVIE_KEY = "movie_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        latestList = Constants.POPULAR_LIST;
        fetchMovies(getURL(pageNum));
        popular.setSelected(true);
    }

    // Basic setup of views
    private void init() {
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
        controlViews(true, false);

        //Set Listeners
        setChipListeners();
        setMovieClickListener();
        addListScrollListener();
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
                        fetchMovies(getURL(pageNum));
                    }
                }
            }
        });
    }

    private String getURL(int page) {
        Uri uri = Uri.parse(latestList).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .appendQueryParameter("page", String.valueOf(page))
                .build();

        return uri.toString();
    }

    // Fetch JSON from the API
    private void fetchMovies(String URL) {
        if (pageNum == 1) {
            controlViews(true, false);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseAndShowInUi(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                controlViews(false, false);
                showError();
            }
        });

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Parse the response and show to the main user interface
    private void parseAndShowInUi(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.optJSONArray("results");

            if (jsonArray.length() > 0) {
                Gson gson = new Gson();

                // Creates a new ArrayList of fetched result
                ArrayList<MovieItem> items = gson.fromJson(jsonArray.toString(),
                        new TypeToken<ArrayList<MovieItem>>() {}.getType());

                // Adds the above ArrayList to main ArrayList which is
                // to be passed
                moviesItems.addAll(items);

                if (moviesList.getAdapter() == null) {
                    adapter = new MoviesAdapter(MainActivity.this, moviesItems);
                    moviesList.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                controlViews(false, true);
            }
        } catch (Exception e) {
            showError();
            controlViews(false, false);
        }
    }

    private void showError() {
        Toast.makeText(MainActivity.this,
                "Please retry after some time!", Toast.LENGTH_SHORT).show();
    }

    // private boolean isOnline(){
    //     ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    //     NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    //     return networkInfo!=null && networkInfo.isConnected();
    // }

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
                latestList = Constants.POPULAR_LIST;
                moviesItems.clear();
                pageNum = 1;
                fetchMovies(getURL(pageNum));
            }
        });

        top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlChip(false, true);
                latestList = Constants.TOP_RATED_LIST;
                moviesItems.clear();
                pageNum = 1;
                fetchMovies(getURL(pageNum));
            }
        });
    }

    // Helper method
    private void controlViews(boolean progress, boolean list) {
        if (progress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (list) {
            moviesList.setVisibility(View.VISIBLE);
        } else {
            moviesList.setVisibility(View.GONE);
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
