package com.ccino.demo.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.ccino.demo.ui.theme.CaseTheme
import com.ccino.demo.ui.widget.Label

private const val TAG = "KotlinActivity"

class KotlinActivity : ComponentActivity() {


    /****************************** 接收者函数类型 start *******************************/
    interface Receiver

    /*   // 可以把 block 想像成 Receiver 的临时扩展属性，参考下面的 flow 代码
       class SafeFlow<T>(private val block: suspend FlowCollector<T>.() -> Unit) : AbstractFlow<T>() {
           override suspend fun collectSafely(collector: FlowCollector<T>) {
               collector.block()
           }
       }*/
    fun test2(receiver: Receiver, block: Receiver.() -> Unit) {
        // 下面两处调用是等价的
        receiver.block()
        block(receiver) // 注意与 test1 的区别: 此处必传 Receiver
    }

    fun Receiver.test(block: Receiver.() -> Unit) {
        block() // 注意与 test2 的区别: 此片不能传 receiver
        this.block()
    }

    private fun receiverFun() {
        test2(object : Receiver {}) {
            Log.d(TAG, "receiverFun: ")
        }
    }

    /****************************** 接收者函数类型 end *******************************/


    @Composable
    fun Case() {
        Column {
            Row {
                Label("接收者函数类型") { receiverFun() }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaseTheme { Case() } }
    }
}