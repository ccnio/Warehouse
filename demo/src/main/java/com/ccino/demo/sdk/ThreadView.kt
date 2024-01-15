package com.ccino.demo.sdk

import android.graphics.Color
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlin.concurrent.thread

private const val TAG = "ThreadView"

/******************************* 子线程创建 view ******************************/
fun createViewInThread(activity: ComponentActivity) {
    thread {
        val textView = TextView(activity).apply { text = "Thread" }
        Looper.prepare()
        activity.windowManager.addView(textView, WindowManager.LayoutParams())
        SystemClock.sleep(3000)
        textView.setBackgroundColor(Color.RED)
        Looper.loop()
    }
}


/******************************* onResume 中子线程更新 ui view ******************************/
fun updateUiViewInThread(view: TextView) {
    thread { view.setTextColor(Color.RED) }
}

class ThreadView {
    private val handler: Handler

    init {
        val handlerThread = HandlerThread("thread-view");
        handlerThread.start()
        handler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                // 处理 view
            }
        }

    }

    // 更新 view
    fun updateView() {
        handler.sendMessage(Message.obtain())
    }
}
