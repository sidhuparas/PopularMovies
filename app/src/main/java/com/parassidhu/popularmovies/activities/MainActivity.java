package com.parassidhu.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.chip.Chip;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.parassidhu.popularmovies.MovieActivity;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.adapters.MoviesAdapter;
import com.parassidhu.popularmovies.models.MoviesItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.parassidhu.popularmovies.utils.ItemClickSupport;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.moviesList) RecyclerView moviesList;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.popular) Chip popular;
    @BindView(R.id.top_rated) Chip top_rated;

    private ArrayList<MoviesItem> moviesItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        fetchMovies(Constants.POPULAR_LIST);
        popular.setSelected(true);
    }

    // Basic setup of views
    private void init() {
        moviesList.setLayoutManager(new GridLayoutManager(this,3));
        setSupportActionBar(toolbar);
        setTitle("");
        controlViews(true,false);
        setChipListeners();
        setMovieClickListener();
    }


    // Receives JSON, parse and show it in main UI
    private void fetchMovies(String URL) {
        controlViews(true,false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.optJSONArray("results");

                            if (jsonArray.length()>0){
                                Gson gson = new Gson();
                                moviesItems =
                                        gson.fromJson(jsonArray.toString(),
                                                new TypeToken<ArrayList<MoviesItem>>(){}.getType());
                                MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, moviesItems);
                                moviesList.setAdapter(adapter);
                                controlViews(false,true);
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "Please retry after some time!", Toast.LENGTH_SHORT).show();
                            controlViews(false,false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                controlViews(false,false);
                Toast.makeText(MainActivity.this,
                        "Please retry after some time!", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

  // private boolean isOnline(){
  //     ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  //     NetworkInfo networkInfo = cm.getActiveNetworkInfo();
  //     return networkInfo!=null && networkInfo.isConnected();
  // }

    // Sets listener for individual movie click
    private void setMovieClickListener(){
        ItemClickSupport.addTo(moviesList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                Bundle bundle = new Bundle();

                MoviesItem item = moviesItems.get(position);
                bundle.putString(Constants.B_BACKDROP, Constants.BASE_BACKDROP + item.getBackdrop_path());
                bundle.putString(Constants.B_POSTER, Constants.BASE_IMAGE + item.getPoster_path());
                bundle.putString(Constants.B_TITLE, item.getTitle());
                bundle.putString(Constants.B_RELEASE_DATE, item.getRelease_date());
                bundle.putString(Constants.B_VOTE_AVERAGE, item.getVote_average());
                bundle.putString(Constants.B_OVERVIEW, item.getOverview());

                intent.putExtra("Values", bundle);

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
               fetchMovies(Constants.POPULAR_LIST);
           }
       });


        top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlChip(false, true);
                fetchMovies(Constants.TOP_RATED_LIST);
            }
        });
    }

    // Helper method
    private void controlViews(boolean progress, boolean list){
        if (progress){
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
    private void controlChip(boolean pop, boolean toprated){

        if (pop){
            popular.setSelected(true);
        } else {
            popular.setSelected(false);
        }

        if (toprated){
            top_rated.setSelected(true);
        } else {
            top_rated.setSelected(false);
        }
    }

}
