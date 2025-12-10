package com.ccino.demo.http.adapter

import com.ccino.demo.http.HttpResult
import com.ccino.demo.http.Resource
import com.ccino.demo.http.WanResp
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResourceCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        // 1. 检查返回值是否是 Call 类型
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

        // 3. 获取 Resource 内部的真实数据类型 T
        if (callInnerType !is ParameterizedType) {
             // Resource<T> 必须有泛型参数
            return null
        }
        val resourceInnerType = getParameterUpperBound(0, callInnerType) // T

        // 4. 决定网络响应类型
        // 如果 T 已经是 HttpResult，则直接使用 T
        // 如果 T 不是 HttpResult，则包装成 Wrapper<T> (默认为 WanResp，或通过注解指定)
        val rawType = getRawType(resourceInnerType)
        val responseType: Type
        val shouldUnwrap: Boolean

        if (HttpResult::class.java.isAssignableFrom(rawType)) {
            responseType = resourceInnerType
            shouldUnwrap = false
        } else {
            // 查找 @ApiWrapper 注解
            val wrapperAnnotation = annotations.firstOrNull { it is ApiWrapper } as? ApiWrapper
            val wrapperClass = wrapperAnnotation?.value?.java ?: WanResp::class.java
            
            responseType = ParameterizedTypeImpl(wrapperClass, arrayOf(resourceInnerType))
            shouldUnwrap = true
        }

        return ResourceCallAdapter<Any>(responseType, shouldUnwrap)
    }

    private class ParameterizedTypeImpl(
        private val raw: Class<*>,
        private val args: Array<Type>
    ) : ParameterizedType {
        override fun getRawType(): Type = raw
        override fun getActualTypeArguments(): Array<Type> = args
        override fun getOwnerType(): Type? = null
    }

    companion object {
        fun create() = ResourceCallAdapterFactory()
    }
}
