package com.parassidhu.popularmovies.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.parassidhu.popularmovies.R
import com.parassidhu.popularmovies.adapters.CastAdapter
import com.parassidhu.popularmovies.adapters.ReviewAdapter
import com.parassidhu.popularmovies.adapters.TrailerAdapter
import com.parassidhu.popularmovies.models.FavoriteMovie
import com.parassidhu.popularmovies.models.MovieItem
import com.parassidhu.popularmovies.utils.Constants
import com.parassidhu.popularmovies.utils.ItemClickSupport
import com.parassidhu.popularmovies.viewmodels.MovieDetailViewModel
import com.parassidhu.popularmovies.viewmodels.MovieDetailViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie.*
import java.util.*

class MovieActivity : AppCompatActivity() {

    private var mViewModel: MovieDetailViewModel? = null
    private var movieItem: MovieItem? = null

    private var cAdapter: CastAdapter? = null
    private var tAdapter: TrailerAdapter? = null
    private var rAdapter: ReviewAdapter? = null

    private var trailerKey: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        ButterKnife.bind(this)

        setSupportActionBar(toolbarMovie)
        title = ""
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)

        getValuesFromIntent()
        setupViewModel()
        setAdapters()
        populateData()
    }

    private fun setAdapters() {
        cAdapter = CastAdapter(this)

        castRcl!!.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        castRcl!!.adapter = cAdapter

        trailerRcl!!.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        //trailerRcl.setAdapter(tAdapter);

        reviewsRcl!!.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        //reviewRcl.setAdapter(rAdapter);

        setTrailerClickListener()
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, MovieDetailViewModelFactory(
                this.application, movieItem!!.id.toString())).get(MovieDetailViewModel::class.java)

        mViewModel!!.isFavorite(movieItem!!.id).observe(this, Observer { integer ->
            if (integer == 1) {
                favorite!!.setFavorite(true, false)
            } else
                favorite!!.setFavorite(false, false)

            setupFavorite()
        })
    }

    // Gets values from Intent and pass to setValuesToViews method
    private fun getValuesFromIntent() {
        if (intent.hasExtra("Values")) {
            val bundle = intent.getBundleExtra("Values")
            movieItem = bundle.getParcelable(MainActivity.MOVIE_KEY)

            assert(movieItem != null)
            val backdrop = Constants.BASE_BACKDROP + movieItem!!.backdropPath
            val poster = Constants.BASE_IMAGE + movieItem!!.posterPath
            val title = movieItem!!.title
            val releaseDate = movieItem!!.releaseDate
            val voteAverage = movieItem!!.voteAverage
            val overview = movieItem!!.overview

            setValuesToViews(backdrop, poster, title, releaseDate, voteAverage, overview)
        }
    }

    // Shows the values in main UI
    private fun setValuesToViews(backdrop_url: String, poster_url: String, title_text: String, release_date: String,
                                 vote_average: String, overview_text: String) {
        Picasso.get().load(backdrop_url)
                .placeholder(R.drawable.img_loading_cover)
                .error(R.drawable.img_loading_error)
                .into(backdrop)

        Picasso.get().load(poster_url)
                .placeholder(R.drawable.img_loading_portrait)
                .error(R.drawable.img_loading_error)
                .into(poster)

        title_tv!!.text = title_text
        releaseDate!!.text = release_date
        overview!!.text = overview_text
        favorites!!.text = vote_average
    }

    private fun setupFavorite() {
        favorite!!.setOnFavoriteChangeListener { buttonView, favorite ->
            if (favorite) {
                mViewModel!!.insertFavMovie(convertToFavorite())
            } else {
                mViewModel!!.deleteFavMovie(convertToFavorite())
            }
        }
    }

    private fun convertToFavorite(): FavoriteMovie {
        val gson = Gson()
        val json = gson.toJson(movieItem)
        return gson.fromJson(json, FavoriteMovie::class.java)
    }

    private fun populateData() {
        mViewModel!!.cast!!.observe(this, Observer { castItems ->
            if (castItems != null && castItems.isNotEmpty())
                cAdapter!!.setCastItems(castItems)
        })

        mViewModel!!.trailers!!.observe(this, Observer { trailerItems ->
            if (trailerItems != null && trailerItems.isNotEmpty()) {
                labelTrailers!!.visibility = View.VISIBLE
                trailerKey = trailerItems[0].id
                if (trailerRcl!!.adapter == null) {
                    tAdapter = TrailerAdapter(this@MovieActivity, trailerItems)
                    trailerRcl!!.adapter = tAdapter
                } else {
                    tAdapter!!.setTrailerItems(trailerItems)
                }
            }
        })

        mViewModel!!.reviews!!.observe(this, Observer { reviewItems ->
            if (reviewItems != null && reviewItems.isNotEmpty()) {
                labelReviews!!.visibility = View.VISIBLE

                if (reviewsRcl!!.adapter == null) {
                    rAdapter = ReviewAdapter(this@MovieActivity, reviewItems)
                    reviewsRcl!!.adapter = rAdapter
                } else {
                    rAdapter!!.setReviewItems(reviewItems)
                }
            }
        })
    }

    private fun setTrailerClickListener() {
        ItemClickSupport.addTo(trailerRcl!!).setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
            override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                val url = Constants.YOUTUBE_BASE + tAdapter!!.getTrailerItems()!![position].id!!

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.share) {
            if (!trailerKey!!.isEmpty()) {
                val trailerUrl = Constants.YOUTUBE_BASE + trailerKey!!
                val text = "Watch Trailer: " + movieItem!!.title + "\n" + trailerUrl
                ShareCompat.IntentBuilder.from(this)
                        .setChooserTitle("Share Trailer Using...")
                        .setType("text/plain")
                        .setText(text).startChooser()
            } else {
                Toast.makeText(this, "There's no trailer to show!",
                        Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
