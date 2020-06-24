package com.ware.http.base

/**
 * Created by jianfeng.li on 19-6-28.
 */
abstract class BaseResp {
    abstract fun oK(): Boolean
    abstract fun getCode(): Int
    abstract fun getMsg(): String?
}