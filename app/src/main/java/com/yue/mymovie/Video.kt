package com.yue.mymovie

import com.google.gson.annotations.SerializedName
import androidx.annotation.NonNull
import java.io.Serializable

data class Video(
    @SerializedName("key")
    val filmVedioKey: String,
    @SerializedName("site")
    val filmVedioSite: String,
    @SerializedName("size")
    val filmVedioSize: String,
    @SerializedName("type")
    val filmVedioType: String
){

    constructor():this("","","","")
}


