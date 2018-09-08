package com.parassidhu.popularmovies.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.parassidhu.popularmovies.R
import com.parassidhu.popularmovies.models.ReviewItem

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

class ReviewAdapter(
        private val context: Context,
        private var reviewItems: List<ReviewItem>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = View.inflate(context, R.layout.row_review, null)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as ReviewViewHolder).bind(i)
    }

    fun getReviewItems(): List<ReviewItem>? {
        return reviewItems
    }

    fun setReviewItems(reviewItems: List<ReviewItem>) {
        this.reviewItems = reviewItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reviewItems!!.size
    }

    internal inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.username)
        @JvmField
        var username: TextView? = null

        @BindView(R.id.review)
        @JvmField
        var review: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(position: Int) {
            val (author, content) = reviewItems!![position]
            username!!.text = author
            review!!.text = content

            itemView.setOnClickListener {
                if (review!!.maxLines == 3) {
                    review!!.maxLines = 200
                } else
                    review!!.maxLines = 3
            }
        }
    }
}
