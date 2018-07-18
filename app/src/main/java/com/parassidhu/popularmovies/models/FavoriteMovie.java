package com.parassidhu.popularmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favorites")
public class FavoriteMovie {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private String voteAverage;

    private String title;

    private String popularity;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String posterPath;

    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    private String backdropPath;

    private String overview;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    private Boolean video;

    @ColumnInfo(name = "sort_by")
    private String sortBy;

    public int getId() {
        return id;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Boolean getVideo() { return video; }

    public String getSortBy() { return sortBy; }

    public void setId(int id) {
        this.id = id;
    }

    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public FavoriteMovie(int id, String voteAverage, String title, String popularity,
                     String posterPath, String backdropPath, String overview, String releaseDate, Boolean video) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.title = title;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.video = video;
    }
}
