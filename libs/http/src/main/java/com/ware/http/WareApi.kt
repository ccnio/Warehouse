package com.ware.http

import com.ware.http.resp.FeedArticle
import com.ware.http.resp.FriendSite
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by jianfeng.li on 19-6-24.
 */
interface WareApi {

    @GET("article/list/{num}/json")
    fun getFeedArticleList(@Path("num") num: Int): Call<FeedArticle>

    @GET("article/list/{num}/json")
    fun getFeedArticleListRx(@Path("num") num: Int): Observable<FeedArticle>

    @GET("article/list/{num}/json")
    suspend fun getFeedArticleListCoroutine(@Path("num") num: Int): FeedArticle


    @GET("friend/json")
    fun getFriendSites(): Call<FriendSite>
}