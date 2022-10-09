package com.ccnio.ware.http.state

import android.util.Log
import com.ccnio.ware.http.resp.StateResult
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by jianfeng.li on 2022/9/27.
 */
private const val TAG = "StateCallFactory"

class StateCallAdapterFactory(private val interceptor: StateResultInterceptor) :
    CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): StateCallAdapter? {
        val param = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(param)
        Log.d(TAG, "return = $returnType, param = $param, rawType = $rawType")
        return if (rawType == StateResult::class.java) {
            val resultType = getParameterUpperBound(0, param as ParameterizedType)
            Log.d(TAG, "result parameterizedType = $resultType")
            StateCallAdapter(resultType, interceptor)
        } else null
    }

    companion object {
        fun create(): StateCallAdapterFactory {
            return StateCallAdapterFactory(HttpResultInterceptor())
        }
    }
}