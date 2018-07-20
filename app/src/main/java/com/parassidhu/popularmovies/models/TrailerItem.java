package com.parassidhu.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class TrailerItem {

    @SerializedName("source")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
