package com.ccino.demo.http.adapter

import com.ccino.demo.http.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResourceCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        // 1. 检查返回值是否是 Call 类型
        // 注意：suspend 函数在 Retrofit 内部会被适配成 Call<T>，但通过 CallAdapter.Factory 拿到的是 Call<T>
        // 如果是直接返回 Resource<T> 的非 suspend 函数，这里可能不是 Call
        // 但根据 Retrofit 2.6+ 对 suspend 的支持，它会寻找 Call<Resource<T>> 的 Adapter。
        // 所以这里的 returnType 应该是 Call<Resource<T>>
        
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        // 2. 检查 Call 内部的泛型是否是 Resource
        if (returnType !is ParameterizedType) {
            return null
        }
        val callInnerType = getParameterUpperBound(0, returnType)
        
        if (getRawType(callInnerType) != Resource::class.java) {
            return null
        }

        // 3. 获取 Resource 内部的真实数据类型 (例如 ApiResponse<ActivityResp>)
        if (callInnerType !is ParameterizedType) {
             // Resource<T> 必须有泛型参数
            return null
        }
        val resourceInnerType = getParameterUpperBound(0, callInnerType)

        return ResourceCallAdapter<Any>(resourceInnerType)
    }

    companion object {
        fun create() = ResourceCallAdapterFactory()
    }
}
