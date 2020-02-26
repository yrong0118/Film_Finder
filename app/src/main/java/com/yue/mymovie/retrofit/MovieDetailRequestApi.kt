package com.yue.mymovie.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDetailRequestApi {
    @GET("movie/{movieId}")
    fun getMovieDetailById(@Path("movieId") movieId: String, @Query("language") language: String, @Query("api_key") api_key: String): Observable<BaseResponse>

}