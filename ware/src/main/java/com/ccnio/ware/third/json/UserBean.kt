package com.ccnio.ware.third.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by jianfeng.li on 2022/8/31.
 */
data class UserBean(val name: String = "cc2", var age: Int)

data class Bean(
    @Json(ignore = true)//序列化时忽略
    val address: String? = null,
    @Json(name = "name_phone")//映射
    val phone: String? = null,
)

@JsonClass(generateAdapter = true)
data class BaseResp<T>(
    val code: Int,
    var msg: String,
    var data: T,
) : HttpResult {
    override fun getReqCode(): Int {
        return code
    }

    override fun getReqMsg(): String? {
        return msg
    }

    override fun succeed(): Boolean {
        return code == 0
    }

}

/**
 * 不需要添加注解
 */
interface HttpResult {
    fun getReqCode(): Int
    fun getReqMsg(): String?
    fun succeed(): Boolean
}

@JsonClass(generateAdapter = true)
data class Hobby(
    val type: String,
    var name: String,
)

@JsonClass(generateAdapter = true)
data class UserInfo(
    val name: String = "cc",
    var age: Int,
    var hobby: List<Hobby>,
)





