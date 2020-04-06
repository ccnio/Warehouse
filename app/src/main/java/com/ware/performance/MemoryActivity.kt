package com.ware.performance

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.ware.R
import com.ware.component.BaseActivity
import java.lang.ref.WeakReference

/**
 *handler 内存泄露的解决办法，具体情况具体分析:https://github.com/Moosphan/Android-Daily-Interview/issues/1
 *1. 把 handler 定义为静态内部类，对外部 activity 的引用使用弱引用的方式
＊2. 在 activity 里的 onDestroy 回调方法中，调用 handler的removeCallbacksAndMessages（null）方法，清除消息队列中message
＊3. 把 handler 单独定义成一个类，不作为非静态内部类存在
 */
class MemoryActivity : BaseActivity() {

    /**
     * handler with LifeCycle
     */
    private val mHandler: LifecycleHandler = LifecycleHandler(this)
    private val mHandler2: Handler = WeakHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
    }

    /**
     * kotlin 默认是静态内部，如果想指定非静态的需要显示加 inner
     */
    class WeakHandler(activity: Activity) : Handler() {
        private val weakReference = WeakReference(activity)
        override fun handleMessage(msg: Message?) {
            weakReference.get()?.let {
                // doSomeThings
            }
        }
    }
}
