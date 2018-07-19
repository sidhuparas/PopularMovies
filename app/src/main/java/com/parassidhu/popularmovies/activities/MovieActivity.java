package com.parassidhu.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
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
        setupViewModel();
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, new MovieViewModelFactory
                (this.getApplication())).get(MovieViewModel.class);

        mViewModel.isFavorite(movieItem.getId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer==1){ favoriteButton.setFavorite(true,false); }
                else favoriteButton.setFavorite(false, false);

                setupFavorite();
            }
        });
    }

    // Gets values from Intent and pass to setValuesToViews method
    private void getValuesFromIntent() {
        if (getIntent().hasExtra("Values")){
            Bundle bundle = getIntent().getBundleExtra("Values");
            movieItem = bundle.getParcelable(MainActivity.MOVIE_KEY);

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

        Picasso.get().load(poster).placeholder(R.drawable.img_loading_portrait).into(iv_poster);

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
                    mViewModel.insertFavMovie(convertToFavorite());
                }else {
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
}
