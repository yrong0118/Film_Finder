package com.yue.mymovie.Chat.ChatModel

import com.google.gson.annotations.SerializedName

data class MovieImgById(
    @SerializedName("file_path")
    val movieImageUrl: String
) {

    constructor() : this("")
}