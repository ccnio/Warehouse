package com.ware.http.data

/**
 * Created by jianfeng.li on 19-6-28.
 */
data class BaseResp<T>(var code: Int, var msg: String, var data: T)