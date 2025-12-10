package com.ccino.demo.http

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WanResp<T>(
    val errorCode: Int,
    var errorMsg: String? = null,
    override var data: T? = null,
) : DataHttpResult<T> {
    override fun code() = errorCode
    override fun msg() = errorMsg
    override fun succeed() = errorCode == 0
}

@JsonClass(generateAdapter = true)
data class MapResp<T>(
    val errCode: Int,
    var errMsg: String? = null,
    val result: T? = null,
) : DataHttpResult<T> {
    override fun code() = errCode
    override fun msg() = errMsg
    override fun succeed() = errCode == 100
    override val data: T? get() = result
}

interface DataHttpResult<T> : HttpResult {
    val data: T?
}

interface HttpResult {
    fun code(): Int
    fun msg(): String?
    fun succeed(): Boolean
}

@JsonClass(generateAdapter = true)
data class Banner(
    val desc: String?,
    val id: Int?,
    @Json(name = "imagePath") val imgUrl: String?,
    val isVisible: Int?,
    val order: Int?,
    val title: String?,
    val type: Int?,
    val url: String?
)
