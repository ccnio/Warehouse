package com.ccnio.ware.http.resp

import com.ccnio.ware.http.copy.HttpExceptionParserHolder

/**
 * Created by jianfeng.li on 2022/9/27.
 */

sealed class StateResult<T> {
    data class Success<T>(val result: T) : StateResult<T>()

    /**
     * server 自定义的错误
     */
    data class ServerError<T>(val result: T) : StateResult<T>()

    /**
     * http 协议错误
     */
    data class HttpError<T>(val code: Int, val message: String) : StateResult<T>()

    /**
     * 网络异常
     */
    data class NetError<T>(val throwable: Throwable) : StateResult<T>()

    /**
     * 其它异常
     */
    data class Exception<T>(
        val throwable: Throwable,
        val message: String = HttpExceptionParserHolder.parse(throwable)
    ) : StateResult<T>()
}
