package com.parassidhu.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.parassidhu.popularmovies.R;
import com.parassidhu.popularmovies.models.CastItem;
import com.parassidhu.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CastAdapter extends RecyclerView.Adapter{

    private Context context;
    private List<CastItem> castItems;

    public CastAdapter(Context context) {
        this.context = context;
        castItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.row_cast, null);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((CastViewHolder)viewHolder).bind(i);
    }

    public void setCastItems(List<CastItem> castItems) {
        this.castItems = castItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return castItems.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image) CircularImageView image;
        @BindView(R.id.name) TextView name;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position){
            CastItem item = castItems.get(position);

            String imageURL = Constants.BASE_IMAGE + item.getImage();

            Picasso.get().load(imageURL)
                    .placeholder(R.drawable.img_loading_portrait)
                    .error(R.drawable.img_loading_error)
                    .into(image);

            name.setText(item.getName());
        }
    }
}
