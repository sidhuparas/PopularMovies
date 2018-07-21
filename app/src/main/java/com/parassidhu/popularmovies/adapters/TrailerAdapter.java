package com.parassidhu.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.models.TrailerItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter{

    private Context context;
    private List<TrailerItem> trailerItems;

    public TrailerAdapter(Context context, List<TrailerItem> trailerItems) {
        this.context = context;
        this.trailerItems = trailerItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.row_trailer, null);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((TrailerViewHolder) viewHolder).bind(i);
    }

    public void setTrailerItems(List<TrailerItem> trailerItems){
        this.trailerItems = trailerItems;
        notifyDataSetChanged();
    }

    public List<TrailerItem> getTrailerItems() {
        return trailerItems;
    }

    @Override
    public int getItemCount() {
        return trailerItems.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image) ImageView image;
        TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position){
            TrailerItem item = trailerItems.get(position);

            String imageURL = "https://img.youtube.com/vi/" + item.getId() + "/0.jpg";

            Picasso.get().load(imageURL)
                    .placeholder(R.drawable.img_loading_cover)
                    .error(R.drawable.img_loading_error)
                    .into(image);
        }
    }
}
