package com.ccnio.ware.http.adapter

import android.util.Log
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Created by jianfeng.li on 2022/9/26.
 */
/**
 * 第三部提供一个CustomCallAdapterFactory用于向Retrofit提供CustomCallAdapter：
 */
private const val TAG = "CustomCallAdapterFactor"

class CustomCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CustomCallAdapter? {

        //获取原始类型
        val rawType = getRawType(returnType)
        Log.d(TAG, "rawType = $rawType")

        //返回值必须是Custom并且带有泛型（参数类型），根据APIService接口中的方法返回值，确定returnType
        //如 CustomCall<String> getCategories();，那确定returnType就是String
        if (rawType == CustomCall::class.java && returnType is ParameterizedType) {
            val callReturnType: Type = getParameterUpperBound(0, returnType)
            Log.d(TAG, "get: returnType = $callReturnType")
            return CustomCallAdapter(callReturnType)
        }
        return null
    }

    companion object {
        fun create(): CustomCallAdapterFactory {
            return CustomCallAdapterFactory()
        }
    }
}