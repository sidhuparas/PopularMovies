package com.parassidhu.popularmovies.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.parassidhu.popularmovies.R
import com.parassidhu.popularmovies.models.MovieItem
import com.parassidhu.popularmovies.utils.Constants
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MoviesAdapter(
        private val context: Context,
        private val moviesItems: ArrayList<MovieItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view = View.inflate(context, R.layout.row_movie, null)
        return MoviesListHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as MoviesListHolder).bind(i)
    }

    override fun getItemCount(): Int {
        return moviesItems.size
    }

    internal inner class MoviesListHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.image)
        @JvmField
        var image: ImageView? = null

        @BindView(R.id.title)
        @JvmField
        var title: TextView? = null

        @BindView(R.id.year)
        @JvmField
        var year: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }

        fun bind(position: Int) {
            val (_, _, posterPath, _, release_date, _, title1) = moviesItems[position]

            val path = Constants.BASE_IMAGE + posterPath

            Picasso.get().load(path)
                    .placeholder(R.drawable.img_loading_portrait)
                    .into(image)

            title!!.text = title1

            val shortYear = getYear(release_date)

            if (!shortYear.isEmpty()) {
                year!!.text = shortYear
            }
        }

        private fun getYear(release_date: String): String {
            val date: Date?
            var years = ""
            try {
                val calendar = Calendar.getInstance()
                date = SimpleDateFormat("yyyy-MM-dd").parse(release_date)
                calendar.time = date
                years = calendar.get(Calendar.YEAR).toString()
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return years
        }

    }
}
