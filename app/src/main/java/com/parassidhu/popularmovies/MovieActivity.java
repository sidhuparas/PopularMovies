package com.parassidhu.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.parassidhu.popularmovies.utils.Constants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getValuesFromIntent();
    }

    // Gets values from Intent and pass to setValuesToViews method
    private void getValuesFromIntent() {
        if (getIntent().hasExtra("Values")){
            String backdrop = customGetExtra(Constants.B_BACKDROP);
            String poster = customGetExtra(Constants.B_POSTER);
            String title = customGetExtra(Constants.B_TITLE);
            String release_date = customGetExtra(Constants.B_RELEASE_DATE);
            String vote_average = customGetExtra(Constants.B_VOTE_AVERAGE);
            String overview = customGetExtra(Constants.B_OVERVIEW);

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

    // Helper method
    private String customGetExtra(String toReturn){
        Bundle bundle = getIntent().getBundleExtra("Values");
        return bundle.getString(toReturn);
    }
}
