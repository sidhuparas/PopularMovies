package com.parassidhu.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class CastItem {

    private String name;

    @SerializedName("profile_path")
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
