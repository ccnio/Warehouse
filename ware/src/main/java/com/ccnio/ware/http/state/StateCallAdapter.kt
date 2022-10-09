package com.ccnio.ware.http.state

import android.util.Log
import com.ccnio.ware.http.resp.StateResult
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Created by jianfeng.li on 2022/9/27.
 */
private const val TAG = "StateCallAdapter"

class StateCallAdapter(
    private val resultType: Type,
    private val interceptor: StateResultInterceptor
) : CallAdapter<Type, Call<StateResult<Type>>> {
    override fun responseType() = resultType //WanResp<T>

    override fun adapt(call: Call<Type>): Call<StateResult<Type>> {
        Log.d(TAG, "adapt: call = $call, resultType = $resultType")
        return StateCallProxy(call, interceptor)
    }
}