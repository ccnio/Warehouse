package com.ccino.demo.kt

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

private val scope = MainScope()

/****************************** select start *******************************/
fun caseSelect() {
    scope.launch {
        val deferred1 = async { task1() }
        val deferred2 = async { task2() }
//        selectUnbiased<String> {  }
// select有多个数据同时到达，select 默认会选择第一个数据，若想要随机选择数据可使用 selectUnbiased
        val ret = select {
            deferred1.onAwait { it }
            deferred2.onAwait { it }
        }
        // ret 虽然能很快返回task1的结果，但并不会终止task2的执行
        Log.d("CaseSelect", "caseSelect: ret=$ret")
    }
}

private suspend fun task1(): String {
    delay(2000)
    Log.d("CaseSelect", "task1 end")
    return "task1"
}

private suspend fun task2(): String {
    delay(4000)
    Log.d("CaseSelect", "task2 end")
    return "task2"
}
/****************************** select end  *******************************/


/******************************
 * channel: Channel 更适合于协程之间的通信
 * *******************************/
fun caseChannel() {
    /**
     *  Channel(
     *     capacity: Int = RENDEZVOUS,
     *     onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
     *     onUndeliveredElement: ((E) -> Unit)? = null
     * )
     */
    val channel = Channel<String>()

    //Channel 的内部有一个缓冲区，如果缓冲区满了，receive() 方法还没有被调用，
    //那么 send() 方法就会挂起，直到缓冲区中的元素被 receive() 函数取走后再继续执行。
    scope.launch {
        repeat(5) {
            channel.send("value:$it")
            Log.d("CaseChannel", "caseChannel:send $it")
        }
    }

    scope.launch {
        repeat(5) { // 一次 receive 只能接收一个值
            delay(2000)
            val receive = channel.receive()
            Log.d("CaseChannel", "caseChannel:receive $receive")
        }
    }
}

