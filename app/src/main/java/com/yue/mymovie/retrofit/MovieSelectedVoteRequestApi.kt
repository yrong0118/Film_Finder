package com.yue.mymovie.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieSelectedVoteRequestApi {
    @GET( "search/keyword?"
       )
    fun getMovieListByKeyword(@Query("query") movieSearchKeyWord: String, @Query("page") page: String, @Query("api_key") api_key: String): Observable<MovieSelectedResponse>
}