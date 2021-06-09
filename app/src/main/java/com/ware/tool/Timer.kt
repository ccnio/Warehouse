package com.ware.tool

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

/**
 * Created by jianfeng.li on 21-6-4.
 */
private const val TAG = "Timer"

class Timer @JvmOverloads constructor(private val interval: Long = DateUtils.SECOND_IN_MILLIS, val handler: Handler = Handler(Looper.getMainLooper())) {
    private var duration = 0L
    private var delay = interval
    private var perStart = 0L
    val emiter = MutableLiveData<Long>()
    private var state = State.None
    private val lifecycleOwner: LifecycleOwner? = null

    private val timerRunnable = Runnable {
        duration += interval
        emiter.value = duration
        perStart = SystemClock.elapsedRealtime()
        timerIncrease()
    }

    private fun timerIncrease() {
        Log.d(TAG, "timerIncrease: delay = $delay")
        handler.postDelayed(timerRunnable, delay)
        if (delay != interval) delay = interval
    }

    fun start(delay: Long = 0) {
        if (state == State.In) {
            Log.w(TAG, "start: already started")
            return
        }
        state = State.In
        handler.postDelayed(timerRunnable, delay)
    }

    fun pause() {
        if (state == State.Pause) {
            Log.w(TAG, "pause: already pause")
            return
        }
        state = State.Pause
        handler.removeCallbacks(timerRunnable)
        delay = maxOf(0L, interval - (SystemClock.elapsedRealtime() - perStart))
    }

    fun cancel() {
        handler.removeCallbacks(timerRunnable)
        duration = 0L
        perStart = 0L
        delay = interval
        state = State.None
    }

    enum class State { None, In, Pause }
}