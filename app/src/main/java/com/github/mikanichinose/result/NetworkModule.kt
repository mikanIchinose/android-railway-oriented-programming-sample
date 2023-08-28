package com.github.mikanichinose.result

import io.getstream.result.call.retrofit.RetrofitCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object NetworkModule {
    private const val BASE_URL = "https://api.github.com/"
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(AuthInterceptor())
        addInterceptor(HttpLoggingInterceptor().setLevel(Level.BODY))
    }.build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
    private val retrofit = retrofitBuilder
        .build()
    private val streamRetrofit = retrofitBuilder
        .addCallAdapterFactory(RetrofitCallAdapterFactory.create())
        .build()

    val streamGithubApi: StreamGithubApi = streamRetrofit.create()
    val githubApi: GithubApi = retrofit.create()
}