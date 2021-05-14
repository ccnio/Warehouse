package com.ware.kt

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by jianfeng.li on 21-5-13.
 */
private const val TAG = "CoroutineDemo"

/**
 * for translate to java
 */
class CoroutineDemo {

    private val scope = CoroutineScope(Dispatchers.Main)

    fun task() {
        scope.launch {
            susFun()
        }
    }

    /**
     * 1. suspend函数原型: Object susFun(@NotNull [Continuation] $completion)
     * 2. suspend 函数必须在 suspend里调用
     */
    private suspend fun susFun() {
        val ret = withContext(Dispatchers.IO) {
            Log.d(TAG, "susFun: ")
            val a = 3
            Log.d(TAG, "susFun: $a")
            a
        }
    }
}