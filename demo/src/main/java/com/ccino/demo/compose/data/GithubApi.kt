package com.ccino.demo.compose.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API 返回的 GitHub 用户数据模型
 */
data class GithubUser(
    val id: Long = 0,
    val login: String = "",
    val name: String? = "",
    @SerializedName("avatar_url")
    val avatarUrl: String? = ""
)

/**
 * Retrofit 接口，定义了我们的 API 端点
 */
interface GithubApiService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GithubUser
}

/**
 * Retrofit 客户端单例
 */
object GithubApiClient {
    private const val BASE_URL = "https://api.github.com/"

    val service: GithubApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(GithubApiService::class.java)
    }
}
