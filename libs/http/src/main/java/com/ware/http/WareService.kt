package com.ware.http

import com.ware.http.resp.FeedArticle
import com.ware.http.resp.FriendSite
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by jianfeng.li on 19-6-24.
 */
interface WareService {

    @GET("article/list/{num}/json")
    fun getFeedArticleList(@Path("num") num: Int): Observable<FeedArticle>


    @GET("friend/json")
    fun getFriendSites(): Observable<FriendSite>
}