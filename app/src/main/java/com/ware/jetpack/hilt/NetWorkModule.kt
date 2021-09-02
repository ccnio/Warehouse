package com.ware.jetpack.hilt

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by ccino on 2021/8/31.
 */
private const val TAG = "HiltNetWork"

@Module
@InstallIn(SingletonComponent::class)
class NetWorkModule {
    @Provides
    @IntoSet
    fun provideStr1(): String {
        return "NetWorkModule str1"
    }

    @Provides
    @IntoSet
    fun provideStr2(): String {
        return "NetWorkModule str2"
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val build = OkHttpClient.Builder().build()
        Log.d(TAG, "provideOkHttpClient: okHttp = $build")
        return build
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        Log.d(TAG, "provideRetrofit: okHttp = $okHttpClient")
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://example.com/")
            .client(okHttpClient)
            .build()
    }
}