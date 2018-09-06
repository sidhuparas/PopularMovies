package com.parassidhu.popularmovies.models

import com.google.gson.annotations.SerializedName

data class CastItem(
        val name: String?,
        @SerializedName("profile_path") val image: String?
)
