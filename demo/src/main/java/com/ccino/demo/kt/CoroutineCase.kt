package com.ccino.demo.kt

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
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