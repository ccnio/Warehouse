package com.ccnio.ware.http.resp

import com.squareup.moshi.JsonClass

/**
 * Created by jianfeng.li on 2022/10/1.
 */


@JsonClass(generateAdapter = true)
data class WanResp<T>(
    val errorCode: Int,
    var errorMsg: String? = null,
    var data: T? = null,
) : HttpResult {
    override fun code() = errorCode
    override fun msg() = errorMsg
    override fun succeed() = errorCode == 10
}

@JsonClass(generateAdapter = true)
data class WanResp2<T>(
    val errorCode: Int,
    var errorMsg: String? = null,
    var data: T? = null,
) : HttpResult {
    override fun code() = errorCode
    override fun msg() = errorMsg
    override fun succeed() = errorCode == 0
}

interface HttpResult {
    fun code(): Int
    fun msg(): String?
    fun succeed(): Boolean
}
