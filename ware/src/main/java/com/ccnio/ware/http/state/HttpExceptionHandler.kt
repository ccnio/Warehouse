package com.ccnio.ware.http.copy

import com.ccnio.ware.http.copy.HttpExceptionParser.Companion.DEFAULT_PARSER
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLHandshakeException

/**
 * 异常处理器持有者
 */
object HttpExceptionParserHolder : HttpExceptionParser {
    var parser = DEFAULT_PARSER

    override fun parse(throwable: Throwable): String {
        return parser.parse(throwable)
    }
}

/**
 * 网络异常解析器
 */
interface HttpExceptionParser {
    /**
     * 将一个异常转化为自然语言报错
     */
    fun parse(throwable: Throwable): String

    companion object {
        val NET_ERROR_TYPES =
            listOf(
                ConnectException::class,
                HttpException::class,
                SocketException::class,
                UnknownHostException::class
            )

        /**
         * 默认实现
         */
        val DEFAULT_PARSER = object : HttpExceptionParser {
            override fun parse(throwable: Throwable): String {
                return when (throwable) {
                    is ConnectException, is SocketException, is HttpException, is UnknownHostException -> "网络连接失败"
                    is SSLHandshakeException -> "证书验证失败"
                    is JSONException, is ParseException, is JsonParseException -> "解析报文失败"
                    is SocketTimeoutException -> "连接超时"
                    is IOException -> throwable.message ?: "未知错误"
                    else -> "未知错误"
                }
            }
        }
    }
}