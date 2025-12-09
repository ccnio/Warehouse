package com.ccino.demo.http.adapter

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


private val httpClient = OkHttpClient.Builder().apply {
    readTimeout(20, TimeUnit.SECONDS)
    writeTimeout(20, TimeUnit.SECONDS)
    connectTimeout(15, TimeUnit.SECONDS)
//    if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor)

}.build()

private const val baseUrl = "https://www.wanandroid.com"
private val retrofit by lazy {
    Retrofit.Builder()
        .client(httpClient)
        .baseUrl(baseUrl)
        .addCallAdapterFactory(ResourceCallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}

fun <T> createService(service: Class<T>): T = retrofit.create(service)
