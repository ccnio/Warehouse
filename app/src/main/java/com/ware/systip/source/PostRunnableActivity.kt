package com.ware.systip.source

import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_post_runnable.*

private const val TAG = "PostRunnableActivity"

class PostRunnableActivity : AppCompatActivity() {
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_runnable)
        Log.d(TAG, "onCreate: ${txtView.width}") // error
        handler.post { Log.d(TAG, "handler run: ${txtView.width}") } //ok
        txtView.post { Log.d(TAG, "view run: ${txtView.width}") } //ok

        /**
         * IdleHandler 可以用来提升性能，主要用在我们希望能够在当前线程消息队列空闲时做些事情（譬如 UI 线程在显示完成后，如果线程空闲我们就可以提前准备其他内容）的情况
         */
        Looper.myQueue().addIdleHandler(object : MessageQueue.IdleHandler {
            override fun queueIdle(): Boolean {
                //返回true就是单次回调后不删除，下次进入空闲时继续回调该方法，false只回调单次。
                Log.d(TAG, "queueIdle: ")
                return false
            }
        })
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d(TAG, "run: ")
            }
        }, 2000)
    }
}