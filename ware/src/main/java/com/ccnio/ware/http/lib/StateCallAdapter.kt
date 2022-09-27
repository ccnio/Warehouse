package com.ccnio.ware.http.lib

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Created by jianfeng.li on 2022/9/27.
 */
class StateCallAdapter(private val callReturnType: Type) : CallAdapter<Type, StateCall<Type>> {
    override fun responseType() = callReturnType

    override fun adapt(call: Call<Type>): StateCall<Type> {
        return StateCall(call)
    }
}