package com.ccnio.ware.http.lib

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by jianfeng.li on 2022/9/27.
 */
class StateCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): StateCallAdapter? {
        val rawType = getRawType(returnType)
        if (rawType == StateCall::class.java && returnType is ParameterizedType) {
            val callReturnType: Type = getParameterUpperBound(0, returnType)
            return StateCallAdapter(callReturnType)
        }
        return null
    }

    companion object {
        fun create(): StateCallAdapterFactory {
            return StateCallAdapterFactory()
        }
    }
}