package com.ccino.demo.kt

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors

private const val TAG = "MutexCase"
private val mutex = Mutex()
private val scope = MainScope()

@Composable
fun MutexCase(modifier: Modifier = Modifier) {
    Row(modifier) {
        Button(onClick = {  SyncProblem().caseSyncProblem() }) {
            Text("多协程同步")
        }
    }
}

//多协程即使在同一线程上也可能出现同步问题
class SyncProblem {
    private var counter = 0 // 共享的计数器
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    // 假设这个函数可能被多个协程并发调用
    fun increment(key: Int) {
        scope.launch {
            val current = counter
            Log.d("SyncProblem", "increment: $key, current: $current")
            delay(50) // 模拟耗时操作
            counter = current + 1 // 竞态条件！结果可能不准确
            Log.d("SyncProblem", "increment: $key, after: $counter")
        }
    }

    fun caseSyncProblem() {
       repeat(10) {
            increment(it)
        }
    }
}


/************************** 不可重入 ************************/
fun testReentrant() {
    val pool = Executors.newCachedThreadPool()
    repeat(2) {
        scope.launch(pool.asCoroutineDispatcher()) {
            mutex.withLock {
                Log.d(TAG, "testReentrant before: ${Thread.currentThread().name}")
                delay(2000)
                Log.d(TAG, "testReentrant after: ${Thread.currentThread().name}")
            }
        }
    }
}
//两个线程按顺序执行
//08:30:00.062  testReentrant before: pool-thread-1
//08:30:02.064  testReentrant after: pool-thread-1
//08:30:02.065  testReentrant before: pool-thread-2
//08:30:04.068  testReentrant after: pool-thread-2


/************************** 保证顺序 ************************/
fun testMutexOrder() {
    scope.launch { job1() }
    scope.launch { job2() }
}

private suspend fun job2() {
    mutex.withLock {
        Log.d(TAG, "job2")
    }
}

private suspend fun job1() {
    mutex.withLock {
        delay(2000)
        Log.d(TAG, "job1")
    }
}


/************************** 死锁 ************************/
fun mutexDeadLock() {
    scope.launch { firstLock() }
}

private suspend fun firstLock() {
    mutex.withLock {
        Log.d(TAG, "firstLock: ")
        secondLock()
    }
}

private suspend fun secondLock() {
    mutex.withLock {
        delay(2000)
        Log.d(TAG, "secondLock")
    }
}