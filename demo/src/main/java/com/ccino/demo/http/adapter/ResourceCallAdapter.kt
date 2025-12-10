package com.ccino.demo.http.adapter

import com.ccino.demo.http.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class ResourceCallAdapter<T>(
    private val responseType: Type,
    private val shouldUnwrap: Boolean
) : CallAdapter<T, Call<Resource<T>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<Resource<T>> {
        // 这里 T 实际上是网络层返回的类型 (e.g., WanResp<List<Banner>>)
        // 但 Call<Resource<T>> 的泛型 T 是 Resource 的泛型。
        // 当 shouldUnwrap=true 时，responseType 是 WanResp<Data>，adapt 需要返回 Call<Resource<Data>>
        // Java/Kotlin 泛型擦除，这里强转一下或者在 ResourceCall 里处理

        return ResourceCall<T>(call as Call<Any>, shouldUnwrap) as Call<Resource<T>>
    }
}
