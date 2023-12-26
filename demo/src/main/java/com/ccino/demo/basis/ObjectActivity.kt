package com.ccino.demo.basis

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.demo.ui.widget.Label
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

private const val TAG = "ObjectActivity"

class ObjectActivity : ComponentActivity() {
    private var obj: Any? = Any()

    private fun reference() {
        lifecycleScope.launch {
            /*
            // 弱引用概念、验证
             val weakReference = WeakReference(obj)
             Log.d(TAG, "reference: value=${weakReference.get()}")
             obj = null // 注掉的话，下面的打印就不会返回空
             delay(1000)
             Runtime.getRuntime().gc() // 手动触发垃圾回收, 使用 System.gc 不会生效
             delay(2000)
             Log.d(TAG, "reference: after gc obj=${weakReference.get()}") // 不会返回空 */

            // 虚引用验证：get 始终返回空，引用栈里一直存储的是 虚引用 对象而非被引用的对象
            val queue = ReferenceQueue<Any>()
            val reference = PhantomReference(obj!!, queue)
            Log.d(TAG, "reference before: value=${reference.get()}, queue=${queue.poll()}, refer=$reference")
            obj = null
            Runtime.getRuntime().gc()
            delay(1000)
            Log.d(TAG, "reference after: ${reference.get()}, queue=${queue.poll()}")
        }
    }

    @Composable
    fun Greeting() {
        Column {
            Row {
                Label("引用") { reference() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Greeting() } }
    }
}