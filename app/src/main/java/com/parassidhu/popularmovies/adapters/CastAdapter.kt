package com.parassidhu.popularmovies.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.mikhaellopez.circularimageview.CircularImageView
import com.parassidhu.popularmovies.R
import com.parassidhu.popularmovies.models.CastItem
import com.parassidhu.popularmovies.utils.Constants
import com.squareup.picasso.Picasso

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

class CastAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var castItems: List<CastItem>? = null

    init {
        castItems = ArrayList()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = View.inflate(context, R.layout.row_cast, null)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as CastViewHolder).bind(i)
    }

    fun setCastItems(castItems: List<CastItem>) {
        this.castItems = castItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return castItems!!.size
    }

    inner class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image)
        @JvmField
        var image: CircularImageView? = null

        @BindView(R.id.name)
        @JvmField
        var name: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(position: Int) {
            val (name1, image1) = castItems!![position]

            val imageURL = Constants.BASE_IMAGE + image1!!

            Picasso.get().load(imageURL)
                    .placeholder(R.drawable.img_loading_portrait)
                    .error(R.drawable.img_loading_error)
                    .into(image)

            name!!.text = name1
        }
    }
}
