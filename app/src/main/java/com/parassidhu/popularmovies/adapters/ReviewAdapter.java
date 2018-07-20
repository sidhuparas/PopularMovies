package com.parassidhu.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.models.ReviewItem;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ReviewItem> reviewItems;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.row_cast, null);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }


    @Override
    public int getItemCount() {
        return reviewItems.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
