package com.parassidhu.popularmovies.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parassidhu.popularmovies.BuildConfig
import com.parassidhu.popularmovies.R
import com.parassidhu.popularmovies.adapters.MoviesAdapter
import com.parassidhu.popularmovies.models.FavoriteMovie
import com.parassidhu.popularmovies.models.MovieItem
import com.parassidhu.popularmovies.utils.Constants
import com.parassidhu.popularmovies.utils.ItemClickSupport
import com.parassidhu.popularmovies.viewmodels.MovieViewModel
import com.parassidhu.popularmovies.viewmodels.MovieViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movie.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val moviesItems = ArrayList<MovieItem>()
    private var mLayoutManager: GridLayoutManager? = null
    private var adapter: MoviesAdapter? = null
    private var pageNum: Int = 0

    private var mViewModel: MovieViewModel? = null

    private val TAG = javaClass.simpleName

    private val compareByPopularity = Comparator<MovieItem> { (_, _, _, _, _, _, _, popularity1), (_, _, _, _, _, _, _, popularity2) ->
        val d = java.lang.Double.valueOf(popularity1) - java.lang.Double.valueOf(popularity2)
        when {
            d > 0 -> -1
            d == 0.0 -> 0
            else -> 1
        }
    }

    private val compareByRating = Comparator<MovieItem> { (_, voteAverage1), (_, voteAverage2) ->
        val d = java.lang.Double.valueOf(voteAverage1) - java.lang.Double.valueOf(voteAverage2)
        when {
            d > 0 -> -1
            d == 0.0 -> 0
            else -> 1
        }
    }

    private val allMoviesObserver: Observer<List<MovieItem>> = Observer { movieItems ->
        if (movieItems != null) {
            if (movieItems.isNotEmpty()) {
                moviesItems.clear()
                sortOfflineMovies(movieItems)

                val map = removeDuplicateValues(movieItems)

                setValuesToAdapter(map.values)
                network_retry_lay!!.visibility = View.GONE
            } else {
                showProgressBar(false)
                network_retry_lay!!.visibility = View.VISIBLE
            }
        }
    }

    private val favoriteMoviesObserver: Observer<List<FavoriteMovie>> = Observer { favoriteMovies ->
        if (isFavoritesSelected) {
            val gson = Gson()
            val json = gson.toJson(favoriteMovies)
            val list = gson.fromJson<List<MovieItem>>(json, object : TypeToken<List<MovieItem>>() {

            }.type)
            moviesItems.clear()
            setValuesToAdapter(list)
            Log.d(TAG, "Retrieved Favorites")

            if (moviesItems.size == 0) {
                Toast.makeText(this@MainActivity, "No Favorites Found!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val isOnline: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = Objects.requireNonNull(cm).activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    private val isFavoritesSelected: Boolean
        get() = favorites_chip!!.isSelected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        init()

        if (savedInstanceState == null) {
            popular!!.isSelected = true
        } else {
            determineChipToSelect(savedInstanceState)
        }

        setupViewModel()
    }

    // Basic setup of views
    private fun init() {
        Stetho.initializeWithDefaults(this)

        // Setup RecyclerView
        var spanCount = 2
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3
        }

        pageNum = 1

        mLayoutManager = GridLayoutManager(this, spanCount)
        moviesList!!.layoutManager = mLayoutManager

        // Setup Custom Toolbar
        setSupportActionBar(toolbar)
        title = ""

        // Show progress bar and hide list
        showProgressBar(true)

        //Set Listeners
        setChipListeners()
        setMovieClickListener()
        addListScrollListener()
    }

    // Handle ViewModel
    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, MovieViewModelFactory(this.application)).get(MovieViewModel::class.java)

        if (!isFavoritesSelected) {
            mViewModel!!.getAllMovies(getURL(pageNum), sortBy).observe(this, allMoviesObserver)
        }
    }

    private fun sortOfflineMovies(movieItems: List<MovieItem>) {
        if (!isOnline) {
            if (sortBy == Constants.POPULAR_LIST)
                Collections.sort(movieItems, compareByPopularity)
            else if (sortBy == Constants.TOP_RATED_LIST)
                Collections.sort(movieItems, compareByRating)
        }
    }

    private fun removeDuplicateValues(movieItems: List<MovieItem>): Map<Int, MovieItem> {
        val map = LinkedHashMap<Int, MovieItem>()

        for (item in movieItems) {
            map[item.id] = item
        }
        return map
    }

    private fun getFavs() {
        mViewModel = ViewModelProviders.of(this, MovieViewModelFactory(this.application)).get(MovieViewModel::class.java)

        mViewModel!!.favoriteMovies.observe(this, favoriteMoviesObserver)
    }

    private fun setValuesToAdapter(list2: Collection<MovieItem>) {
        moviesItems.addAll(list2)
        showInUI()
        showProgressBar(false)
    }


    private fun showInUI() {
        if (moviesList!!.adapter == null) {
            adapter = MoviesAdapter(this@MainActivity, moviesItems)
            moviesList!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun addListScrollListener() {
        moviesList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Total Items
                if (!isFavoritesSelected) {
                    val count = mLayoutManager!!.itemCount

                    if (dy > 0 && count / 20 == pageNum) {
                        val holderCount = mLayoutManager!!.childCount
                        val oldCount = mLayoutManager!!.findFirstVisibleItemPosition()

                        // Checks if user is on the verge of end of the list
                        if (holderCount + oldCount >= count - 10) {
                            // Increments the page number and fetch the result
                            pageNum++
                            mViewModel!!.fetchMovies(getURL(pageNum), sortBy)
                            Log.d(TAG, "onScrolled: $pageNum")
                        }
                    }
                }
            }
        })
    }

    private fun getURL(page: Int): String {
        val uri = Uri.parse(sortBy).buildUpon()
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .appendQueryParameter("page", page.toString())
                .build()

        return uri.toString()
    }

    // Sets listener for individual movie click
    private fun setMovieClickListener() {
        ItemClickSupport.addTo(moviesList!!).setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
            override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                val intent = Intent(this@MainActivity, MovieActivity::class.java)
                val bundle = Bundle()

                val item = moviesItems[position]

                bundle.putParcelable(MOVIE_KEY, item)
                intent.putExtra("Values", bundle)

                // Shared Element Transition
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity, v, resources.getString(R.string.img_trans))

                startActivity(intent, options.toBundle())
            }
        })
    }

    // Set listener for click of "Popular" or "Top Rated"
    private fun setChipListeners() {
        popular!!.setOnClickListener {
            controlChip(true, false, false)
            sortBy = Constants.POPULAR_LIST
            reset()
            newChipClick()
        }

        top_rated!!.setOnClickListener {
            controlChip(false, true, false)
            sortBy = Constants.TOP_RATED_LIST
            reset()
            newChipClick()
        }

        favorites_chip!!.setOnClickListener {
            controlChip(false, false, true)
            sortBy = Constants.FAVORITES_KEY
            reset()
            getFavs()
            // newChipClick();
        }
    }

    private fun newChipClick() {
        if (isOnline) {
            setupViewModel()
            mViewModel!!.getAllMovies(getURL(pageNum), sortBy)
        } else {
            mViewModel!!.getAllMovies(getURL(pageNum), sortBy).removeObserver(allMoviesObserver)
            setupViewModel()
            //mViewModel.decideNetworkRequestOrExisting();
            showProgressBar(false)
        }
    }

    // Executes when user switches between Pop,Top_Rated or Favs
    private fun reset() {
        moviesItems.clear()
        pageNum = 1
        moviesList!!.adapter = null
        showProgressBar(true)
    }

    // Helper method
    private fun showProgressBar(progress: Boolean) {
        if (progress) {
            progressBar!!.visibility = View.VISIBLE
        } else {
            progressBar!!.visibility = View.GONE
        }
    }

    // Helper method
    private fun controlChip(pop: Boolean, toprated: Boolean, fav: Boolean) {
        popular!!.isSelected = pop

        top_rated!!.isSelected = toprated

        favorites_chip!!.isSelected = fav
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        when {
            popular!!.isSelected -> outState.putString(MOVIE_KEY, Constants.POPULAR_LIST)
            top_rated!!.isSelected -> outState.putString(MOVIE_KEY, Constants.TOP_RATED_LIST)
            else -> outState.putString(MOVIE_KEY, Constants.FAVORITES_KEY)
        }
    }

    @OnClick(R.id.retry_btn)
    fun retry() {
        network_retry_lay!!.visibility = View.GONE
        showProgressBar(true)
        newChipClick()
    }

    // After configuration changes, select a chip
    private fun determineChipToSelect(savedInstanceState: Bundle) {
        val selectedChip = savedInstanceState.getString(MOVIE_KEY, Constants.POPULAR_LIST)

        when (selectedChip) {
            Constants.POPULAR_LIST -> controlChip(true, false, false)
            Constants.TOP_RATED_LIST -> controlChip(false, true, false)
            else -> {
                getFavs()
                moviesItems.clear()
                controlChip(false, false, true)
            }
        }
    }

    companion object {
        private var sortBy = Constants.POPULAR_LIST  // Stores user-selected URL, Popular Movies or Top-Rated
        const val MOVIE_KEY = "movie_item"
    }
}
