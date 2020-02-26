package com.yue.mymovie

import android.content.Context
import android.widget.Toast
import com.yue.mymovie.retrofit.MovieVedioesRequestApi
import com.yue.mymovie.retrofit.RetrofitClient
import com.yue.mymovie.retrofit.VediosResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class MovieGluManagerComingSoon {
    val okHttpClient: OkHttpClient

    init{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
        builder.addInterceptor(logging)
        okHttpClient = builder.build()
    }

    fun retrieveMovie(
        contentType:String,
        primaryReleaseDateGte:String,
        primaryReleaseDateLte:String,
        api_key:String,
        authorization:String,
        language:String,
        imaFrontPAth:String,
        n: String,
        context: Context
    ):List<Movie> {
        val request=Request.Builder()
        .url("https://api.themoviedb.org/3/discover/movie?api_key=$api_key&primary_release_date.gte=$primaryReleaseDateGte&primary_release_date.lte=$primaryReleaseDateLte&language=$language")
            .headers(Headers.headersOf("Authorization",authorization,"Content-Type",contentType))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val moviesString: String? = response.body?.string()
        val movieList = mutableListOf<Movie>()

        if (response.isSuccessful && !moviesString.isNullOrEmpty()){
            val jsonObject = JSONObject(moviesString)
            val films = jsonObject.getJSONArray("results")
            for (i in 0 until films.length()){
                val filmJson = films.getJSONObject(i)
                val filmId = filmJson.getString("id")
                val filmName = filmJson.getString("title")
                val imageUrl = imaFrontPAth+filmJson.getString("poster_path")
                //if no image?
                val releaseDate = filmJson.getString("release_date")
//                val releaseDate = ""

                var vedioes:List<Video> = emptyList()

                val movie = Movie(
                    filmName,
                    filmId,
                    imageUrl,
                    releaseDate,
                    "",
                    "",
                    "",
                    vedioes)

                getData(filmId,api_key,language,movie,context)



                movieList.add(movie)
            }
        }
        return movieList
    }


    fun getData(selectedMovieId : String, api:String,language: String, movie: Movie,context: Context){
        var movieVedioesRequestApi = RetrofitClient.instance.create(MovieVedioesRequestApi::class.java)
        movieVedioesRequestApi.getVedioesById(selectedMovieId,language,api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> detail(baseResponse,movie) },
                { throwable ->
                    //*******************************
                    if (context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(context, "Movies Video API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }
    fun detail(vedioResponse: VediosResponse,movie: Movie){
        movie.vedios = vedioResponse.results
    }

}

