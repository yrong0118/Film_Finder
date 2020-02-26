package com.yue.mymovie

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("title")
    var title : String,
    @SerializedName("id")
    val id : String,
    @SerializedName("poster_path")
    val imageUrl : String,
    @SerializedName("release_date")
    val releaseDate : String,
    var category: String,
    @SerializedName("overview")
    var description :String,
    @SerializedName("vote_average")
    var rating:String,
    var vedios:List<Video>
){

    constructor():this("","","","","","","", emptyList())
}
