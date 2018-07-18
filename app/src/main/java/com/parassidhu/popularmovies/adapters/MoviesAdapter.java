package com.parassidhu.popularmovies.adapters;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.activities.MainActivity;
import com.parassidhu.popularmovies.database.MovieRepository;
import com.parassidhu.popularmovies.models.MovieItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MovieItem> moviesItems;

    public MoviesAdapter(Context context, ArrayList<MovieItem> moviesItems) {
        this.context = context;
        this.moviesItems = moviesItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.row_movie, null);
        return new MoviesListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MoviesListHolder) viewHolder).bind(i);
    }

    @Override
    public int getItemCount() {
        return moviesItems.size();
    }

    class MoviesListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image) ImageView image;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.year) TextView year;

        MoviesListHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(final int position) {
            MovieItem item = moviesItems.get(position);

            String path = Constants.BASE_IMAGE + item.getPosterPath();

            Picasso.get().load(path)
                    .into(image);

            String release_date = item.getReleaseDate();
            title.setText(item.getTitle());

            String s_year = getYear(release_date);

            if (!s_year.isEmpty()) {
                year.setText(s_year);
            }
        }

        String getYear(String release_date) {
            Date date = null;
            String years = "";
            try {
                Calendar calendar = Calendar.getInstance();
                date = new SimpleDateFormat("yyyy-MM-dd").parse(release_date);
                calendar.setTime(date);
                years = String.valueOf(calendar.get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return years;
        }

    }
}
