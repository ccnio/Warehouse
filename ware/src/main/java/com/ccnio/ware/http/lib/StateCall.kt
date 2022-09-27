package com.ccnio.ware.http.lib

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by jianfeng.li on 2022/9/27.
 */
private const val TAG = "StateCall"

class StateCall<R>(call: Call<R>) {
    init {
        call.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                Log.d(TAG, "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }
}