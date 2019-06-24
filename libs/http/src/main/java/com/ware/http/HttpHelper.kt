package com.ware.http

import com.ware.http.data.FeedArticle
import com.ware.http.data.FriendSite
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by jianfeng.li on 19-6-24.
 */
object HttpHelper {
    private val mRetrofit = Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun getFeedArticleList(): Call<FeedArticle> {
        val wareApi = mRetrofit.create(WareApi::class.java)
        return wareApi.getFeedArticleList(10)
    }

    fun getFriendSites(): Call<FriendSite> {
        val wareApi = mRetrofit.create(WareApi::class.java)
        return wareApi.getFriendSites()
    }
}