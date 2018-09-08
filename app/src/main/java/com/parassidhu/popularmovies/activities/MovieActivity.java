package com.parassidhu.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.gson.Gson;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.adapters.CastAdapter;
import com.parassidhu.popularmovies.adapters.ReviewAdapter;
import com.parassidhu.popularmovies.adapters.TrailerAdapter;
import com.parassidhu.popularmovies.models.CastItem;
import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.models.ReviewItem;
import com.parassidhu.popularmovies.models.TrailerItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.parassidhu.popularmovies.utils.ItemClickSupport;
import com.parassidhu.popularmovies.viewmodels.MovieDetailViewModel;
import com.parassidhu.popularmovies.viewmodels.MovieDetailViewModelFactory;
import com.parassidhu.popularmovies.viewmodels.MovieViewModel;
import com.parassidhu.popularmovies.viewmodels.MovieViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity {

    @BindView(R.id.backdrop) ImageView iv_backdrop;
    @BindView(R.id.poster) ImageView iv_poster;
    @BindView(R.id.title) TextView tv_title;
    @BindView(R.id.releaseDate) TextView tv_release_date;
    @BindView(R.id.overview) TextView tv_overview;
    @BindView(R.id.favorites) TextView tv_fav;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.favorite) MaterialFavoriteButton favoriteButton;

    @BindView(R.id.castRcl) RecyclerView castRcl;
    @BindView(R.id.trailerRcl) RecyclerView trailerRcl;
    @BindView(R.id.reviewsRcl) RecyclerView reviewRcl;

    @BindView(R.id.labelTrailers) TextView labelTrailers;
    @BindView(R.id.labelReviews) TextView labelReviews;

    private MovieDetailViewModel mViewModel;
    private MovieItem movieItem;

    private CastAdapter cAdapter;
    private TrailerAdapter tAdapter;
    private ReviewAdapter rAdapter;

    private String trailerKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getValuesFromIntent();
        setupViewModel();
        setAdapters();
        populateData();
    }

    private void setAdapters() {
        cAdapter = new CastAdapter(this);

        castRcl.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        castRcl.setAdapter(cAdapter);

        trailerRcl.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        //trailerRcl.setAdapter(tAdapter);

        reviewRcl.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        //reviewRcl.setAdapter(rAdapter);

        setTrailerClickListener();
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, new MovieDetailViewModelFactory(
                this.getApplication(), String.valueOf(movieItem.getId()))).get(MovieDetailViewModel.class);

        mViewModel.isFavorite(movieItem.getId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == 1) {
                    favoriteButton.setFavorite(true, false);
                } else favoriteButton.setFavorite(false, false);

                setupFavorite();
            }
        });
    }

    // Gets values from Intent and pass to setValuesToViews method
    private void getValuesFromIntent() {
        if (getIntent().hasExtra("Values")) {
            Bundle bundle = getIntent().getBundleExtra("Values");
            movieItem = bundle.getParcelable(MainActivity.MOVIE_KEY);

            assert movieItem != null;
            String backdrop = Constants.BASE_BACKDROP + movieItem.getBackdropPath();
            String poster = Constants.BASE_IMAGE + movieItem.getPosterPath();
            String title = movieItem.getTitle();
            String release_date = movieItem.getReleaseDate();
            String vote_average = movieItem.getVoteAverage();
            String overview = movieItem.getOverview();

            setValuesToViews(backdrop, poster, title, release_date, vote_average, overview);
        }
    }

    // Shows the values in main UI
    private void setValuesToViews(String backdrop, String poster, String title, String release_date,
                                  String vote_average, String overview) {
        Picasso.get().load(backdrop)
                .placeholder(R.drawable.img_loading_cover)
                .error(R.drawable.img_loading_error)
                .into(iv_backdrop);

        Picasso.get().load(poster)
                .placeholder(R.drawable.img_loading_portrait)
                .error(R.drawable.img_loading_error)
                .into(iv_poster);

        tv_title.setText(title);
        tv_release_date.setText(release_date);
        tv_overview.setText(overview);
        tv_fav.setText(vote_average);
    }

    private void setupFavorite() {
        favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite) {
                    mViewModel.insertFavMovie(convertToFavorite());
                } else {
                    mViewModel.deleteFavMovie(convertToFavorite());
                }
            }
        });
    }

    private FavoriteMovie convertToFavorite() {
        Gson gson = new Gson();
        String json = gson.toJson(movieItem);
        return gson.fromJson(json, FavoriteMovie.class);
    }

    private void populateData() {
        mViewModel.getCast().observe(this, new Observer<List<CastItem>>() {
            @Override
            public void onChanged(@Nullable List<CastItem> castItems) {
                if (castItems != null && castItems.size() != 0)
                    cAdapter.setCastItems(castItems);
            }
        });

        mViewModel.getTrailers().observe(this, new Observer<List<TrailerItem>>() {
            @Override
            public void onChanged(@Nullable List<TrailerItem> trailerItems) {
                if (trailerItems != null && trailerItems.size() != 0) {
                    labelTrailers.setVisibility(View.VISIBLE);
                    trailerKey = trailerItems.get(0).getId();
                    if (trailerRcl.getAdapter() == null) {
                        tAdapter = new TrailerAdapter(MovieActivity.this, trailerItems);
                        trailerRcl.setAdapter(tAdapter);
                    } else {
                        tAdapter.setTrailerItems(trailerItems);
                    }
                }
            }
        });

        mViewModel.getReviews().observe(this, new Observer<List<ReviewItem>>() {
            @Override
            public void onChanged(@Nullable List<ReviewItem> reviewItems) {
                if (reviewItems != null && reviewItems.size() != 0) {
                    labelReviews.setVisibility(View.VISIBLE);

                    if (reviewRcl.getAdapter() == null) {
                        rAdapter = new ReviewAdapter(MovieActivity.this, reviewItems);
                        reviewRcl.setAdapter(rAdapter);
                    } else {
                        rAdapter.setReviewItems(reviewItems);
                    }
                }
            }
        });
    }

    private void setTrailerClickListener() {
        ItemClickSupport.Companion.addTo(trailerRcl).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String URL = Constants.YOUTUBE_BASE + tAdapter.getTrailerItems().get(position).getId();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(URL));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share){
            if (!trailerKey.isEmpty()) {
                String trailerUrl = Constants.YOUTUBE_BASE + trailerKey;
                String text = "Watch Trailer: " + movieItem.getTitle() + "\n" + trailerUrl;
                ShareCompat.IntentBuilder.from(this)
                        .setChooserTitle("Share Trailer Using...")
                        .setType("text/plain")
                        .setText(text).startChooser();
            }else {
                Toast.makeText(this, "There's no trailer to show!",
                        Toast.LENGTH_SHORT).show();
            }
        }
            return super.onOptionsItemSelected(item);
    }
}
