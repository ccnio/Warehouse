package com.ccnio.ware.http.state

import com.ccnio.ware.http.resp.HttpResult
import com.ccnio.ware.http.resp.StateResult

/**
 * Created by jianfeng.li on 2022/10/8.
 */
class HttpResultInterceptor : StateResultInterceptor {
    override fun <T> onIntercept(result: StateResult<T>): StateResult<T> {
        if (result is StateResult.Success && result.result is HttpResult) {
            val httpResult = result.result as HttpResult
            if (!httpResult.succeed()) {
                return StateResult.ServerError(result.result)
            }
        }
        return result
    }
}