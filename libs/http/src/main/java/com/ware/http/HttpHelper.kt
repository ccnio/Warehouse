package com.ware.http

import com.ware.http.resp.FeedArticle
import com.ware.http.resp.FriendSite
import io.reactivex.Observable
import retrofit2.Call

/**
 * Created by jianfeng.li on 19-6-24.
 */
const val HTTP_WAN_ANDROID = "https://www.wanandroid.com/"

object HttpHelper {
    const val HTTP_UNDEFINED_CODE = 11111
    const val HTTP_ERROR_CODE = 22222
    const val HTTP_NO_NET_CODE = 22223

    private val mRetrofit = RetrofitFactory.instance()

    fun getFeedArticleList(): Call<FeedArticle> {
        val wareApi = mRetrofit.getService(WareApi::class.java, HTTP_WAN_ANDROID)
        return wareApi.getFeedArticleList(10)
    }

    fun getFeedArticleListRx(): Observable<FeedArticle> {
        val wareApi = mRetrofit.getService(WareApi::class.java, HTTP_WAN_ANDROID)
        return wareApi.getFeedArticleListRx(10)
    }

    suspend fun getFeedArticleListCoroutine(): FeedArticle {
        val wareApi = mRetrofit.getService(WareApi::class.java, HTTP_WAN_ANDROID)
        return wareApi.getFeedArticleListCoroutine(10)
    }

    fun getFriendSites(): Call<FriendSite> {
        val wareApi = mRetrofit.getService(WareApi::class.java, HTTP_WAN_ANDROID)
        return wareApi.getFriendSites()
    }
}