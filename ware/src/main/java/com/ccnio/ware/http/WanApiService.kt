package com.ccnio.ware.http

import com.ccnio.ware.http.adapter.CustomCall
import com.ccnio.ware.http.resp.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by ccino on 2021/12/17.
 */
//API 请求接口
interface WanApiService {
    /**
     * 原始 Call 返回类型
     */
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<Repo>>

    /**
     * Rxjava 返回类型的 adapter
     */
    @GET("article/list/{num}/json")
    fun getFeedArticleListRx(@Path("num") num: Int): Observable<Repo>

    /**
     * 协程 直接返回 resp 的adapter
     */
    @GET("users/{user}/repos")
    suspend fun getRepos(@Path("user") user: String): List<Repo>


    /**
     * 自定义的 adapter
     */
    @GET("project/tree/json")
    fun getProjectTree(): CustomCall<ProjectRes>

    @GET("project/tree/json2")
    suspend fun getStateProjectTree(): StateResult<WanResp<List<ProjectInfo>>>

    @GET("project/tree/json")
    suspend fun getStateProjectTree2(): WanResp2<List<ProjectInfo>>
}