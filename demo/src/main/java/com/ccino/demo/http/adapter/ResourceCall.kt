package com.ccino.demo.http.adapter

import com.ccino.demo.http.HttpResult
import com.ccino.demo.http.Resource
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResourceCall<T>(private val delegate: Call<T>) : Call<Resource<T>> {

    override fun enqueue(callback: Callback<Resource<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val url = call.request().url.toString()

                val resource: Resource<T> = try {
                    if (response.isSuccessful) {
                        when (val body = response.body()) {
                            is HttpResult -> {
                                if (body.code() == 0) {
                                    Resource.Success(body)
                                } else {
                                    // 如果是业务错误，生成 Resource.Error
                                    val errMsg = body.msg() ?: "服务异常"
                                    val result = Resource.Error(errCode = body.code(), errMsg = errMsg)
                                    result
                                }
                            }

                            else -> Resource.Success(body)
                        }
                    } else {
                        Resource.Error(errCode = response.code(), throwable = Exception(response.message()))
                    }
                } catch (e: Exception) {
                    val result = Resource.Error(throwable = e)
                    // 只有非 CancellationException 才上报
                    result
                }
                // 因为我们要返回的是 Resource<T>，对于Retrofit来说这本身是一个成功的响应（即使Resource内部是Error）
                callback.onResponse(this@ResourceCall, Response.success(resource))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val result = Resource.Error(throwable = t)
                callback.onResponse(this@ResourceCall, Response.success(result))
            }
        })
    }

    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
    override fun clone(): Call<Resource<T>> = ResourceCall(delegate.clone())
    override fun execute(): Response<Resource<T>>? = null

}
