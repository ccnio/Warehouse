package com.ccino.demo.http

import java.net.HttpRetryException
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.SocketException
import java.net.URISyntaxException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLException


sealed interface Resource<out T> {

    class Success<T>(val data: T? = null) : Resource<T>

    class Error(
        val errCode: Int? = null,
        val errMsg: String? = null,
        val throwable: Throwable? = null
    ) : Resource<Nothing> {

        fun isNetworkError(): Boolean {
            return when (throwable) {
                is SocketException,
                is HttpRetryException,
                is MalformedURLException,
                is ProtocolException,
                is UnknownHostException,
                is UnknownServiceException,
                is URISyntaxException,
                is SSLException -> true

                else -> false
            }
        }

        override fun toString(): String {
            return when {
                !errMsg.isNullOrEmpty() -> errMsg
                throwable != null -> if (isNetworkError()) "网络异常" else "服务异常"
                else -> "服务异常"
            }
        }
    }
}