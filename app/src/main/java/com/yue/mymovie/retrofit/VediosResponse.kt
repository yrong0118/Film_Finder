package com.yue.mymovie.retrofit

import com.google.gson.annotations.SerializedName
import com.yue.mymovie.Video

data class VediosResponse(
        @SerializedName("results")
        val results: List<Video>
)