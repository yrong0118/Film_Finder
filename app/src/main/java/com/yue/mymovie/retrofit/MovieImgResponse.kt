package com.yue.mymovie.retrofit

import com.google.gson.annotations.SerializedName
import com.yue.mymovie.Chat.ChatModel.MovieImgById

data class MovieImgResponse(
    @SerializedName("posters")
    val results: List<MovieImgById>
)