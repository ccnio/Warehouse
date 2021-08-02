package com.ware.kt

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by jianfeng.li on 20-3-23.
 */
private const val TAG = "User"
fun notBlock() {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        Log.d(TAG, "before ${Thread.currentThread().name}")
        delay(1000)
        Log.d(TAG, "after")
    }
    Log.d(TAG, "hello world")
}