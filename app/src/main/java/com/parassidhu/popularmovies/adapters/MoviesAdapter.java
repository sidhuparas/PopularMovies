package com.parassidhu.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.parassidhu.popularmovies.R;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public MoviesAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.row_movie, viewGroup);
        return new MoviesListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private class MoviesListHolder extends RecyclerView.ViewHolder {

        MoviesListHolder(View view) {
            super(view);
        }
    }
}
