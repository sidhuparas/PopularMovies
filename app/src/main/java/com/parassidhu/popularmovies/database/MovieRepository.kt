package com.parassidhu.popularmovies.database

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parassidhu.popularmovies.BuildConfig
import com.parassidhu.popularmovies.models.*
import com.parassidhu.popularmovies.utils.Constants
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MovieRepository(private val application: Application) {

    private val movieDao: MovieDao
    private val allMovies: LiveData<List<MovieItem>>
    private val TAG = this.javaClass.simpleName
    private val moviesItems = ArrayList<MovieItem>()

    private var recentSortBy = Constants.POPULAR_LIST
    private val result = MutableLiveData<List<MovieItem>>()

    val favoriteMovies: LiveData<List<FavoriteMovie>>
        get() = movieDao.favoriteMovies


    val isOnline: Boolean
        get() {
            val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = Objects.requireNonNull(cm).activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    init {
        val db = MovieDatabase.getDatabase(application)
        movieDao = db!!.movieDao()
        allMovies = movieDao.getMovies(recentSortBy)
    }

    fun getAllMovies(sort_by: String): LiveData<List<MovieItem>> {
        return movieDao.getMovies(sort_by)
    }

    fun insertFavMovie(movie: FavoriteMovie) {
        InsertFavAsync(movieDao).execute(movie)
    }

    fun deleteFavMovie(movie: FavoriteMovie) {
        DeleteFavAsync(movieDao).execute(movie)
    }

    private fun insertMovies(movies: List<MovieItem>) {
        insertAsyncTask(movieDao).execute(movies)
    }

    fun isFavorite(id: Int): LiveData<Int> {
        return movieDao.isFavorite(id)
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: MovieDao) : AsyncTask<List<MovieItem>, Void, Void>() {

        override fun doInBackground(vararg params: List<MovieItem>): Void? {
            try {
                mAsyncTaskDao.insertMovies(params[0])
            } catch (ignored: Exception) {
            }

            return null
        }
    }

    private class InsertFavAsync internal constructor(private val mAsyncTaskDao: MovieDao) : AsyncTask<FavoriteMovie, Void, Void>() {

        override fun doInBackground(vararg params: FavoriteMovie): Void? {
            mAsyncTaskDao.insertFavoriteMovie(params[0])
            return null
        }
    }

    private class DeleteFavAsync internal constructor(private val mAsyncTaskDao: MovieDao) : AsyncTask<FavoriteMovie, Void, Void>() {

        override fun doInBackground(vararg params: FavoriteMovie): Void? {
            mAsyncTaskDao.deleteFavoriteMovie(params[0])
            return null
        }
    }

    // Fetch JSON from the API
    fun fetchMovies(URL: String, sortBy: String): MutableLiveData<List<MovieItem>> {
        val stringRequest = StringRequest(Request.Method.GET, URL,
                Response.Listener { response -> result.value = parseAndShowInUi(response, sortBy) }, Response.ErrorListener {
            //result.setValue(movieDao.getMovies().getValue());
            result.value = ArrayList()
            Log.d(TAG, "onErrorResponse: Failed")
        })

        stringRequest.setShouldCache(false)
        val requestQueue = Volley.newRequestQueue(application.applicationContext)
        requestQueue.add(stringRequest)

        return result
    }

    // Parse the response and show to the main user interface
    private fun parseAndShowInUi(response: String, sortBy: String): List<MovieItem>? {
        try {
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.optJSONArray("results")

            if (jsonArray.length() > 0) {
                val gson = Gson()

                // Creates a new ArrayList of fetched result
                val items = ArrayList<MovieItem>()
                for (i in 0 until jsonArray.length()) {
                    val item = gson.fromJson(jsonArray.optJSONObject(i).toString(),
                            MovieItem::class.java)
                    item.sortBy = sortBy
                    items.add(item)
                }
                // Adds the above ArrayList to main ArrayList which is
                // to be passed

                if (recentSortBy != sortBy) {
                    moviesItems.clear()
                    recentSortBy = sortBy
                }

                moviesItems.addAll(items)

                insertMovies(moviesItems)
                Log.d(TAG, "loadFromNetwork: Done")
                return moviesItems

            }
        } catch (e: JSONException) {
            return null
        }

        return null
    }

    fun getCast(id: String): LiveData<List<CastItem>> {
        val result = MutableLiveData<List<CastItem>>()
        val URL = "http://api.themoviedb.org/3/movie/" + id + "/casts?api_key=" + BuildConfig.API_KEY

        val stringRequest = StringRequest(Request.Method.GET, URL, Response.Listener { response ->
            try {
                val obj = JSONObject(response)
                val jsonArray = obj.optJSONArray("cast")

                if (jsonArray.length() > 0) {
                    val gson = Gson()
                    val list = gson.fromJson<List<CastItem>>(jsonArray.toString(),
                            object : TypeToken<List<CastItem>>() {

                            }.type)

                    result.value = list
                }
            } catch (ignored: Exception) {
            }
        }, Response.ErrorListener { })

        val requestQueue = Volley.newRequestQueue(application)
        requestQueue.add(stringRequest)

        return result
    }

    fun getTrailers(id: String): LiveData<List<TrailerItem>> {
        val result = MutableLiveData<List<TrailerItem>>()
        val URL = "http://api.themoviedb.org/3/movie/" + id + "/trailers?api_key=" + BuildConfig.API_KEY

        val stringRequest = StringRequest(Request.Method.GET, URL, Response.Listener { response ->
            try {
                val obj = JSONObject(response)
                val jsonArray = obj.optJSONArray("youtube")

                if (jsonArray.length() > 0) {
                    val gson = Gson()
                    val list = gson.fromJson<List<TrailerItem>>(jsonArray.toString(),
                            object : TypeToken<List<TrailerItem>>() {

                            }.type)

                    result.value = list
                }
            } catch (ignored: Exception) {
            }
        }, Response.ErrorListener { })

        val requestQueue = Volley.newRequestQueue(application)
        requestQueue.add(stringRequest)

        return result
    }

    fun getReviews(id: String): LiveData<List<ReviewItem>> {
        val result = MutableLiveData<List<ReviewItem>>()
        val URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + BuildConfig.API_KEY

        val stringRequest = StringRequest(Request.Method.GET, URL, Response.Listener { response ->
            try {
                val obj = JSONObject(response)
                val jsonArray = obj.optJSONArray("results")

                if (jsonArray.length() > 0) {
                    val gson = Gson()
                    val list = gson.fromJson<List<ReviewItem>>(jsonArray.toString(),
                            object : TypeToken<List<ReviewItem>>() {

                            }.type)

                    result.value = list
                }
            } catch (ignored: Exception) {
            }
        }, Response.ErrorListener { })

        val requestQueue = Volley.newRequestQueue(application)
        requestQueue.add(stringRequest)

        return result
    }
}
