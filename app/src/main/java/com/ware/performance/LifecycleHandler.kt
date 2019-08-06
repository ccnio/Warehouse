package com.ware.performance

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


/**
 * Created by jianfeng.li on 19-8-6.
 */
class LifecycleHandler @JvmOverloads constructor(private val lifecycleOwner: LifecycleOwner, callback: Callback? = null, looper: Looper = Looper.myLooper())
    : Handler(looper, callback), LifecycleObserver {
    init {
        addObserver()
    }

    private fun addObserver() {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        removeCallbacksAndMessages(null)
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}