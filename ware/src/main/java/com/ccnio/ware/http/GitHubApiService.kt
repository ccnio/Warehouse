package com.ccnio.ware.http

import com.ccnio.ware.http.adapter.CustomCall
import com.ccnio.ware.http.lib.StateCall
import com.ccnio.ware.http.resp.ProjectRes
import com.ccnio.ware.http.resp.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by ccino on 2021/12/17.
 */
//API 请求接口
interface GitHubApiService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<Repo>>

    @GET("users/{user}/repos")
    suspend fun getRepos(@Path("user") user: String): List<Repo>


    @GET("project/tree/json")
    fun getProjectTree(): CustomCall<ProjectRes>

    @GET("project/tree/json")
    fun getStateProjectTree(): StateCall<ProjectRes>
}