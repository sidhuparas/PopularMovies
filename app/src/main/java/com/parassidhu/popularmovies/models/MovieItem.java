package com.parassidhu.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieItem implements  Parcelable{

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vote_count);
        dest.writeString(this.id);
        dest.writeString(this.vote_average);
        dest.writeString(this.title);
        dest.writeString(this.popularity);
        dest.writeString(this.poster_path);
        dest.writeString(this.backdrop_path);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeValue(this.video);
        dest.writeValue(this.adult);
    }

    protected MovieItem(Parcel in) {
        this.vote_count = in.readString();
        this.id = in.readString();
        this.vote_average = in.readString();
        this.title = in.readString();
        this.popularity = in.readString();
        this.poster_path = in.readString();
        this.backdrop_path = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.video = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.adult = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}
