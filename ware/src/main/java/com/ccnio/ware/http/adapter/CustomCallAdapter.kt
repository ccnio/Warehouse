package com.ccnio.ware.http.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type


/**
 * Created by jianfeng.li on 2022/9/26.
 */

/**
 * 第二步定义CustomCallAdapter实现 【【【Call<T> 到 CustomCall<T>的转换】】】
 * responseType: 业务数据类型, CustomCall<T> 中的 T
 */
class CustomCallAdapter(private val responseType: Type) : CallAdapter<Type, CustomCall<Type>> {
    /**
     * 真正数据的类型，如Call<T> 中的 T，这个T会作为  Converter.Factory.responseConverter的第一个参数
     */
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<Type>): CustomCall<Type> {
        return CustomCall(call)
    }
}