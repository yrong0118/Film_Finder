package com.yue.mymovie.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        val BASE_URL = "https://api.themoviedb.org/3/"

        val instance: Retrofit
        init {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient)
                .build()
        }


        val getHttpClient: OkHttpClient
            get() {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                var builder: OkHttpClient.Builder = OkHttpClient.Builder()
                    .addInterceptor(MvoiesInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                return builder.build()
            }


        private class MvoiesInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response { //To change body of created functions use File | Settings | File Templates.
                var original: Request = chain.request()
                var request: Request = original
                    .newBuilder()
                    .build()
                return chain.proceed(request)
            }


        }
    }
}