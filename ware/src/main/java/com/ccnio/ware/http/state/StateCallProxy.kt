package com.ccnio.ware.http.state

import android.util.Log
import com.ccnio.ware.http.resp.StateResult
import com.ccnio.ware.http.resp.toExceptionState
import com.ccnio.ware.http.resp.toStateResult
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Created by jianfeng.li on 2022/9/27.
 */
private const val TAG = "StateCallProxy"

//copy from DefaultCallAdapterFactory.java
class StateCallProxy<T>(
    private val delegate: Call<T>,
    private val interceptor: StateResultInterceptor
) : Call<StateResult<T>> {
    override fun enqueue(callback: Callback<StateResult<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                Log.d(TAG, "onResponse: $response")
                if (delegate.isCanceled) {
                    // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
                    onFailure(call, IOException("Canceled"))
                } else {
                    callback.onResponse(
                        this@StateCallProxy,
                        Response.success(response.toStateResult(interceptor))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@StateCallProxy, Response.success(t.toExceptionState()))
                Log.d(TAG, "onFailure: $t")
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted
    override fun execute(): Response<StateResult<T>>? = null
    override fun cancel() = delegate.cancel()
    override fun isCanceled() = delegate.isCanceled
    override fun clone() = StateCallProxy(delegate.clone(), interceptor)
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}