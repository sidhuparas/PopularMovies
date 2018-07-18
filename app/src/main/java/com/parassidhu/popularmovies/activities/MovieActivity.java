package com.parassidhu.popularmovies.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.gson.Gson;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.parassidhu.popularmovies.viewmodels.MovieViewModel;
import com.parassidhu.popularmovies.viewmodels.MovieViewModelFactory;
import com.squareup.picasso.Picasso;

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

    private MovieViewModel mViewModel;
    private MovieItem movieItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getValuesFromIntent();
        setupFavorite();
        setupViewModel();
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, new MovieViewModelFactory
                (this.getApplication())).get(MovieViewModel.class);

       /* mViewModel.getFavoriteMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovie> favoriteMovies) {

            }
        });*/
    }

    // Gets values from Intent and pass to setValuesToViews method
    private void getValuesFromIntent() {
        if (getIntent().hasExtra("Values")){
            Bundle bundle = getIntent().getBundleExtra("Values");
            MovieItem item = bundle.getParcelable(MainActivity.MOVIE_KEY);

            String backdrop = Constants.BASE_BACKDROP + item.getBackdropPath();
            String poster = Constants.BASE_IMAGE + item.getPosterPath();
            String title = item.getTitle();
            String release_date = item.getReleaseDate();
            String vote_average = item.getVoteAverage();
            String overview = item.getOverview();

            movieItem = item;

            setValuesToViews(backdrop, poster, title, release_date, vote_average, overview);
        }
    }

    // Shows the values in main UI
    private void setValuesToViews(String backdrop, String poster, String title, String release_date,
                                  String vote_average, String overview) {
        Picasso.get().load(backdrop).into(iv_backdrop);
        Picasso.get().load(poster).into(iv_poster);

        tv_title.setText(title);
        tv_release_date.setText(release_date);
        tv_overview.setText(overview);
        tv_fav.setText(vote_average);
    }

    private void setupFavorite(){
        favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite){
                    Gson gson = new Gson();
                    String json = gson.toJson(movieItem);
                    FavoriteMovie favoriteMovie = gson.fromJson(json, FavoriteMovie.class);

                    mViewModel.insertFavMovie(favoriteMovie);
                }
            }
        });
    }
}
