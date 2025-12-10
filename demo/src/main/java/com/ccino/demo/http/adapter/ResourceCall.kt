package com.ccino.demo.http.adapter

import com.ccino.demo.http.DataHttpResult
import com.ccino.demo.http.HttpResult
import com.ccino.demo.http.Resource
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
class ResourceCall<T>(
    private val delegate: Call<Any>,
    private val shouldUnwrap: Boolean
) : Call<Resource<T>> {

    override fun enqueue(callback: Callback<Resource<T>>) {
        delegate.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val resource: Resource<T> = try {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (shouldUnwrap) {
                            if (body is DataHttpResult<*>) {
                                if (body.succeed()) {
                                    Resource.Success(body.data as T)
                                } else {
                                    Resource.Error(errCode = body.code(), errMsg = body.msg())
                                }
                            } else { 
                                Resource.Error(throwable = Exception("Response type mismatch: expected DataHttpResult"))
                            }
                        } else {
                            // Legacy mode
                            when (body) {
                                is HttpResult -> {
                                    if (body.succeed()) {
                                        // 这里如果 body 是 WanResp<T>，需要根据情况返回。
                                        // 但 Legacy 模式下 Resource<T> 的 T 就是 WanResp<List<Banner>> 本身
                                        Resource.Success(body as T)
                                    } else {
                                        val errMsg = body.msg() ?: "服务异常"
                                        Resource.Error(errCode = body.code(), errMsg = errMsg)
                                    }
                                }
                                else -> {
                                    Resource.Success(body as T)
                                }
                            }
                        }
                    } else {
                        Resource.Error(errCode = response.code(), throwable = Exception(response.message()))
                    }
                } catch (e: Exception) {
                    Resource.Error(throwable = e)
                }
                callback.onResponse(this@ResourceCall, Response.success(resource))
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
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
    override fun clone(): Call<Resource<T>> = ResourceCall(delegate.clone(), shouldUnwrap)
    override fun execute(): Response<Resource<T>>? = null

}
