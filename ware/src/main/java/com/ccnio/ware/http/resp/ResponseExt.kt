package com.ccnio.ware.http.resp

import com.ccnio.ware.http.copy.HttpExceptionParser
import com.ccnio.ware.http.state.StateResultInterceptor
import retrofit2.Response

/**
 * Created by jianfeng.li on 2022/9/27.
 */

fun <T> Throwable.toExceptionState(): StateResult<T> =
    if (this::class in HttpExceptionParser.NET_ERROR_TYPES) StateResult.NetError(this)
    else StateResult.Exception(this)

fun <T> Response<T>.toStateResult(interceptor: StateResultInterceptor): StateResult<T> =
    interceptor.onIntercept(if (isSuccessful) toSuccessState() else toHttpErrorState())

fun <T> Response<T>.toSuccessState(): StateResult.Success<T> =
    StateResult.Success(requireNotNull(this.body()))

fun <T> Response<T>.toHttpErrorState(): StateResult.HttpError<T> =
    StateResult.HttpError(this.code(), this.message())

