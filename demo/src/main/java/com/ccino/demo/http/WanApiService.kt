package com.ccino.demo.http

import retrofit2.http.GET

interface WanApiService {
    @GET("banner/json")
    suspend fun getBanner(): Resource<WanResp<List<Banner>>>
}