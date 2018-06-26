package com.parassidhu.popularmovies.models;

public class MoviesItem {

    private String vote_count, id, vote_average, title, popularity, poster_path, backdrop_path, overview, release_date;

    private Boolean video, adult;

    public String getVote_count() {
        return vote_count;
    }

    public String getId() {
        return id;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return title;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public Boolean getVideo() {
        return video;
    }

    public Boolean getAdult() {
        return adult;
    }
}
