package com.ccino.demo.http

import com.ccino.demo.http.adapter.ApiWrapper
import retrofit2.http.GET

interface WanApiService {
    @GET("banner/json")
    suspend fun getBanner(): Resource<List<Banner>>

    @GET("banner/json")
    suspend fun getBanner2(): Resource<WanResp<List<Banner>>>

    @ApiWrapper(MapResp::class)
    @GET("map/json")
    suspend fun getMapData(): Resource<List<Banner>>

    @GET("map/json")
    suspend fun getMapData2(): Resource<MapResp<List<Banner>>>
}
