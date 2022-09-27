package com.ccnio.ware.http.adapter

import retrofit2.Call


/**
 * Created by jianfeng.li on 2022/9/26.
 */

/**
 * 第一步定义CustomCall，专门处理返回CustomCall的数据
 */
class CustomCall<R>(private val call: Call<R>) {

    fun getBodyData(): R? {
        return call.execute().body()
    }
}