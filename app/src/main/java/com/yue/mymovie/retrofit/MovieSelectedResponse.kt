package com.yue.mymovie.retrofit

import com.google.gson.annotations.SerializedName
import com.yue.mymovie.Chat.ChatModel.MovieByKW


data class MovieSelectedResponse(
    @SerializedName("results")
    val results: List<MovieByKW>
)