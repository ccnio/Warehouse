package com.ccnio.ware.http.state

import com.ccnio.ware.http.resp.StateResult

/**
 * Created by jianfeng.li on 2022/10/8.
 */
interface StateResultInterceptor {
    fun <T> onIntercept(result: StateResult<T>): StateResult<T>
}