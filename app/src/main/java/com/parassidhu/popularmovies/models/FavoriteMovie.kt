package com.parassidhu.popularmovies.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorites")
data class FavoriteMovie(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "vote_average") @SerializedName("vote_average") val voteAverage: String,
        @ColumnInfo(name = "poster_path") @SerializedName("poster_path") val posterPath: String,
        @ColumnInfo(name = "backdrop_path") @SerializedName("backdrop_path") val backdropPath: String,
        @ColumnInfo(name = "release_date") @SerializedName("release_date") val releaseDate: String,
        val title: String,
        val popularity: String,
        val overview: String,
        val video: Boolean?
)
