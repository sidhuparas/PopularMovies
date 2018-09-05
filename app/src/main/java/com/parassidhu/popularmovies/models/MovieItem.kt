package com.parassidhu.popularmovies.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "movies")
@Parcelize
data class MovieItem(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "vote_average") @SerializedName("vote_average") val voteAverage: String,
        @ColumnInfo(name = "poster_path") @SerializedName("poster_path") val posterPath: String,
        @ColumnInfo(name = "backdrop_path") @SerializedName("backdrop_path") val backdropPath: String,
        @ColumnInfo(name = "release_date") @SerializedName("release_date") val releaseDate: String,
        @ColumnInfo(name = "sort_by") var sortBy: String,
        val title: String,
        val popularity: String,
        val overview: String,
        val video: Boolean
) : Parcelable
