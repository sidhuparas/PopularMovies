package com.parassidhu.popularmovies.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.parassidhu.popularmovies.R
import com.parassidhu.popularmovies.models.TrailerItem
import com.squareup.picasso.Picasso

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

class TrailerAdapter(
        private val context: Context,
        private var trailerItems: List<TrailerItem>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = View.inflate(context, R.layout.row_trailer, null)
        return TrailerViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as TrailerViewHolder).bind(i)
    }

    fun setTrailerItems(trailerItems: List<TrailerItem>) {
        this.trailerItems = trailerItems
        notifyDataSetChanged()
    }

    fun getTrailerItems(): List<TrailerItem>? {
        return trailerItems
    }

    override fun getItemCount(): Int {
        return trailerItems!!.size
    }

    internal inner class TrailerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image)
        @JvmField
        var image: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(position: Int) {
            val (id) = trailerItems!![position]

            val imageURL = "https://img.youtube.com/vi/$id/0.jpg"

            Picasso.get().load(imageURL)
                    .placeholder(R.drawable.img_loading_cover)
                    .error(R.drawable.img_loading_error)
                    .into(image)
        }
    }
}
