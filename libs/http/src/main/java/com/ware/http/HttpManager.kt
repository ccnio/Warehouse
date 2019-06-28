package com.ware.http

import com.ware.http.data.FeedArticle
import com.ware.http.data.FriendSite
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by jianfeng.li on 19-6-26.
 */
object HttpManager {
    private val mRetrofit = Retrofit.Builder().baseUrl("https://www.wanandroid.com/")
            .client(initClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    private val mWareService by lazy { getService(WareService::class.java) }

    private fun initClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
        return builder.build()
    }

    fun <T> getService(clazz: Class<T>): T {
        return mRetrofit.create(clazz)
    }

    fun getFeedArticleList(): Observable<FeedArticle> {
        return mWareService.getFeedArticleList(10)
    }

    fun getSites(): Observable<FriendSite> {
        return mWareService.getFriendSites()
    }
}