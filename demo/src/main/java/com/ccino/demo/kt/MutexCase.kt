package com.ccino.demo.kt

import android.util.Log
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