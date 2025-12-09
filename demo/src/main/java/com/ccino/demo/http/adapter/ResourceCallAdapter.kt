package com.ccino.demo.http.adapter

import com.ccino.demo.http.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResourceCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Call<Resource<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<Resource<T>> {
        return ResourceCall(call)
    }
}
