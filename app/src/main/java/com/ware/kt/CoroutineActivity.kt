package com.ware.kt

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityCoroutineBinding
import com.ware.jetpack.viewbinding.viewBinding
import kotlinx.android.synthetic.main.activity_coroutine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

private const val TAG_L = "CoroutineActivityL"

/**
 * - delay/ SystemClock.sleep 区别: delay 是suspend函数 执行时会挂起所在协程,时间到后再恢复,不会阻塞其它线程. sleep 休眠当前线程,会阻塞当前线程.
 * - 协程可以延迟启动
 * - GlobalScope.launch 默认在 Dispatchers.Default 上执行
 *
 */
class CoroutineActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope { //by MainScope() way 4
    private val binding by viewBinding(ActivityCoroutineBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)
        coroutineJob = Job()

        binding.scopeView.setOnClickListener(this)
        binding.blockingView.setOnClickListener(this)
        binding.notBlockView.setOnClickListener(this)
        binding.launchView.setOnClickListener(this)
        binding.nestCoroutineView.setOnClickListener(this)
        binding.asyncMulTaskView.setOnClickListener(this)

        tv4.setOnClickListener(this)
        tv5.setOnClickListener(this)
        tv6.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.scope_view -> scope()
            R.id.blocking_view -> blocking()
            R.id.not_block_view -> notBlock()
            R.id.launch_view -> launchView()
            R.id.nest_coroutine_view -> nestCoroutine()
            R.id.asyncMulTaskView -> mulTaskSameTime()
            R.id.tv4 -> {
                runBlocking {
                    val channel = Channel<String>()
                    launch {
                        delay(1000)
                        channel.send("apple")
                    }
                    Log.d(TAG_L, "receive before")
                    val receive = channel.receive()
                    Log.d(TAG_L, "I like $receive")
                    Log.d(TAG_L, "receive after")
                }
            }
            R.id.tv5 -> {
                runBlocking {
                    var time = measureTimeMillis {
                        val one = doSomethingUsefulOne("sync")
                        val two = doSomethingUsefulTwo("sync")
                        Log.d(TAG_L, "The answer is ${one + two}")
                    }
                    Log.d(TAG_L, "Sync completed in $time ms")

                    time = measureTimeMillis {
                        val one = async { doSomethingUsefulOne("async") }
                        val two = async { doSomethingUsefulTwo("async") }
                        Log.d(TAG_L, "The answer is ${one.await() + two.await()}")
                    }
                    Log.d(TAG_L, "Async completed in $time ms")
                }
            }
            R.id.tv6 -> {
                Thread(Runnable {
                    for (i in 0..10) {
                        GlobalScope.launch {
                            delayOp(i)
                        }
                    }
                }).start()
            }
        }
    }

    private fun notBlock() {
        launch {
            Log.d(TAG_L, "before; thread = ${Thread.currentThread().name}")//2 main
            delay(2000)
            Log.d(TAG_L, "after; thread = ${Thread.currentThread().name}")//3 main
        }
        Log.d(TAG_L, "hello world") //1
    }

    private fun blocking() {
        runBlocking {
            GlobalScope.launch {
                delay(1000L)
                Log.d(TAG_L, "World! ${Thread.currentThread().name}") //3
            }
            Log.d(TAG_L, "Hello,") //1
            runBlocking {
                Log.d(TAG_L, "block2 start") //2
                delay(2000L)
                Log.d(TAG_L, "block2 end") //4
            }
            Log.d(TAG_L, "block2 after") //5
        }
        Log.d(TAG_L, "end") //6
    }

    private fun launchView() {
        val job: Job = GlobalScope.launch(context = Dispatchers.Default, start = CoroutineStart.LAZY, block = {
            Log.d(TAG_L, "World! ${Thread.currentThread().name}")
        })
        Log.d(TAG_L, "Hello,")
        job.start()
    }

    private lateinit var coroutineJob: Job
    override val coroutineContext: CoroutineContext //way 1
        get() = Dispatchers.Main + coroutineJob //取消协程通过 coroutineJob.cancel 或者 CoroutineScope的扩展函数cancel

    private val mainScope = MainScope()//way 2
    private val scope = CoroutineScope(Dispatchers.Default) //way3
    private fun scope() {
        launch { Log.d(TAG_L, "scope: way 1") }
        scope.launch { Log.d(TAG_L, "scope way 2: ${Thread.currentThread().name}") }
        mainScope.launch { Log.d(TAG_L, "scope way 3: ${Thread.currentThread().name}") }
    }


    private fun nestCoroutine() {
        GlobalScope.launch(Dispatchers.Main) {
            launch(Dispatchers.Main) {
                delay(1000)
                Log.d(TAG_L, "globalScopeLaunch1: 等待一秒弹出 ")
            }
            Log.d(TAG_L, "globalScopeLaunch1: 立马弹出~~~")
            delay(5000)
            Log.d(TAG_L, "globalScopeLaunch1: 等待五秒弹出~~~~~~")
        }
    }

    private fun mulTaskSameTime() {
        /**
         * async 代码块会新启动一个子协程后立即执行，并且返回一个 Deferred 类型的值，调用它的 await 方法后会暂停当前协程，
         * 直到获取到 async 代码块执行结果，当前协程才会继续执行。
         */
        launch {
            val one = async(Dispatchers.IO) {
                Log.d(TAG_L, "mulTaskSameTime task one : ${Thread.currentThread().name}")
                doSomethingUsefulOne("async")
            }
            val two = async(Dispatchers.IO) {
                Log.d(TAG_L, "mulTaskSameTime task two : ${Thread.currentThread().name}")
                doSomethingUsefulTwo("async")
            }
            Log.d(TAG_L, "mulTaskSameTime: before ${Thread.currentThread().name}")
            val awaitTwo = two.await()
//            Log.d(TAG_L, "The answer is ${one.await() + two.await()}")
            val awaitOne = one.await()
            Log.d(TAG_L, "mulTaskSameTime: $awaitOne  $awaitTwo")
            /**
            2020-03-22 22:28:31.486 13009-13009/com.ware D/CoroutineActivity: mulTaskSameTime: before main
            2020-03-22 22:28:31.486 13009-13085/com.ware D/CoroutineActivity: mulTaskSameTime task one : DefaultDispatcher-worker-2
            2020-03-22 22:28:31.487 13009-13087/com.ware D/CoroutineActivity: mulTaskSameTime task two : DefaultDispatcher-worker-4
            2020-03-22 22:28:33.489 13009-13009/com.ware D/CoroutineActivity: mulTaskSameTime: end main
             */
        }
    }

    private suspend fun delayOp(id: Int) {
        delay(2000)
        Log.d(TAG_L, "delay $id ${Thread.currentThread().name}")
    }

    private suspend fun doSomethingUsefulOne(type: String): Int {
        delay(2000L)
        Log.d(TAG_L, "doSomethingUsefulOne $type")
        return 13
    }

    private suspend fun doSomethingUsefulTwo(type: String): Int {
        delay(1000L)
        Log.d(TAG_L, "doSomethingUsefulTwo $type")
        return 29
    }

    override fun onDestroy() {
        super.onDestroy()
        //取消掉所有协程内容
        cancel()
        coroutineJob.cancel()
        scope.cancel()
    }
}
