package com.yue.mymovie.retrofit

import android.media.tv.TvContentRating
import com.google.gson.annotations.SerializedName
import com.yue.mymovie.Movie

data class BaseResponse(
    @SerializedName("id")
    val filmId: String,
    @SerializedName("title")
    val filmName: String,
    @SerializedName("poster_path")
    val filmImg: String,
    @SerializedName("vote_average")
    val rating: String,
    @SerializedName("overview")
    val description:String,
    @SerializedName("release_date")
    val releaseDate:String
)