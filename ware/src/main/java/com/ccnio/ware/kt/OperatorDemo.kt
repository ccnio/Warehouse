package com.ccnio.ware.kt

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

/**
 * Created by ccino on 2022/3/3.
 */
private const val TAG = "OperatorDemo"

class OperatorDemo {
    fun case() {
        val instance = OperatorDemo()
        instance({ Log.d(TAG, "case: invoke operator") }, Continuation(Dispatchers.IO) { Log.d(TAG, "case: resume") })
    }

    //重载 invoke，instance() 等价于 instance.invoke(). 例子来源： CoroutineStart .
    operator fun <T> invoke(block: suspend () -> T, completion: Continuation<T>) {
        //startCoroutine 扩展函数: fun <T> (suspend () -> T).startCoroutine(completion: kotlin.coroutines.Continuation<T>): kotlin.Unit
        block.startCoroutine(completion)
    }
}