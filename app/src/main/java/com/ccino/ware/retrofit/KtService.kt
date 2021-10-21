package com.ccino.ware.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by ccino on 2021/10/18.
 */
interface KtService {

    @GET("friend/json")
    suspend fun getFriendLink(): FriendLinkRes

    companion object {
        private const val BASE_URL = "https://wanandroid.com/"

        fun create(): KtService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KtService::class.java)
        }
    }
}