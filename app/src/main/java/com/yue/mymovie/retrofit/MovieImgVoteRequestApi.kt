package com.yue.mymovie.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieImgVoteRequestApi {
    @GET("movie/{movieId}/images?")
    fun getMovieImgById(@Path("movieId") movieId: String,  @Query("api_key") api_key: String): Observable<MovieImgResponse>
}
