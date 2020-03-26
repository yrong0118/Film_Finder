package com.yue.mymovie.Chat.ChatModel

import com.google.gson.annotations.SerializedName

data class MovieByKW(
    @SerializedName("name")
    var movieName: String,
    @SerializedName("id")
    val movieId: String,
    var movieImageUrl: String
) {
    constructor() : this("", "", "")
}

data class MovieByKWVote(
    @SerializedName("name")
    var movieName: String,
    @SerializedName("id")
    val movieId: String,
    var movieImageUrl: String,
    var selected: Boolean
) {

    constructor() : this("", "", "",false)
}

data class MovieVote(
    val movieId: String,
    var supportNum: Int) {
    constructor(): this("", 0)
}