package com.ware.kt

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_coroutine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.system.measureTimeMillis

class CoroutineActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope by MainScope() {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv7 -> {
                /**
                 * 按顺序执行，执行线程不同
                 */
                GlobalScope.launch(Dispatchers.Main) {
                    Log.d("CoroutineActivity", "thread ${Thread.currentThread().name}")
                    val a = withContext(Dispatchers.IO) { getDataA(1) }
                    val resultA = doSomethingOnMainA(a)
                    val b = withContext(Dispatchers.IO) { getDataB(resultA) }
                    val resultB = doSomethingOnMainB(b)
                    Log.d("CoroutineActivity", "onClick: $resultB")
                }
            }
            R.id.tv4 -> {
                runBlocking {
                    val channel = Channel<String>()
                    launch {
                        delay(1000)
                        channel.send("apple")
                    }
                    println("receive before")
                    val receive = channel.receive()
                    println("I like $receive")
                    println("receive after")
                }
            }
            R.id.tv5 -> {
                runBlocking {
                    var time = measureTimeMillis {
                        val one = doSomethingUsefulOne("sync")
                        val two = doSomethingUsefulTwo("sync")
                        println("The answer is ${one + two}")
                    }
                    println("Sync completed in $time ms")

                    time = measureTimeMillis {
                        val one = async { doSomethingUsefulOne("async") }
                        val two = async { doSomethingUsefulTwo("async") }
                        println("The answer is ${one.await() + two.await()}")
                    }
                    println("Async completed in $time ms")
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
            else -> {

            }
        }
    }

    private fun doSomethingOnMainB(b: Int): Int {
        Toast.makeText(this, "mainB $b", Toast.LENGTH_SHORT).show()
        return b
    }

    private fun doSomethingOnMainA(n: Int): Int {
        Log.d("CoroutineActivity", "doSomethingOnMainA thread ${Thread.currentThread().name}")
        Toast.makeText(this, "mainA $n", Toast.LENGTH_SHORT).show()
        return n
    }

    private suspend fun getDataA(n: Int): Int {
        delay(2000)
        Log.d("CoroutineActivity", "getDataA thread ${Thread.currentThread().name}")
        Log.d("CoroutineActivity", "getDataA: $n")
        return n + 10
    }

    private suspend fun getDataB(n: Int): Int {
        delay(2000)
        Log.d("CoroutineActivity", "getDataB thread ${Thread.currentThread().name}")
        Log.d("CoroutineActivity", "getDataB: $n")
        return n
    }

    private suspend fun delayOp(id: Int) {
        delay(2000)
        println("delay $id ${Thread.currentThread().name}")
    }

    private suspend fun doSomethingUsefulOne(type: String): Int {
        delay(2000L)
        println("doSomethingUsefulOne $type")
        return 13
    }

    private suspend fun doSomethingUsefulTwo(type: String): Int {
        delay(1000L)
        println("doSomethingUsefulTwo $type")
        return 29
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)
        tv7.setOnClickListener(this)
        tv1.setOnClickListener {
            GlobalScope.launch {
                Log.d("CoroutineActivity", "onCreate: before")

                delay(1000)
                Log.d("CoroutineActivity", "onCreate: after")
            }
            Log.d("CoroutineActivity", "hello world")
        }

        tv2.setOnClickListener {
            val job: Job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                println("World!")
            }
            println("Hello,")
            job.start()
            Thread.sleep(2000L)
        }

        tv3.setOnClickListener {
            runBlocking {
                GlobalScope.launch {
                    delay(1000L)
                    println("World!")
                }
                println("Hello,")
                runBlocking {
                    println("block2 start")
                    delay(2000L)
                    println("block2 end")
                }
            }
            println("end")
        }

        tv4.setOnClickListener(this)
        tv5.setOnClickListener(this)
        tv6.setOnClickListener(this)


        val deferred = GlobalScope.async {
            var str = ""
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("http://www.wanandroid.com/friend/json")
                    .build()
            val response = client.newCall(request).execute()
            response.run {
                body()?.let {
                    val ins = it.string()
                    str = ins
                }
            }
            delay(5000)
//            str
            "abcdefa"
        }

//        runBlocking {
//            Toast.makeText(this@CoroutineActivity, deferred.await(), Toast.LENGTH_SHORT) //deferred.await()
//        }
//        println("end")


        //加载并显示数据
        loadDataAndShow()
    }


    var job: Job? = null

    private fun loadDataAndShow() {
        job = GlobalScope.launch(Dispatchers.IO) {
            //IO 线程里拉取数据
            var result = fetchData()

            withContext(Dispatchers.Main) {
                //主线程里更新 UI
                Toast.makeText(this@CoroutineActivity, result, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchData(): String {
        delay(2000)
        return "content"
    }

    override fun onDestroy() {
        super.onDestroy()
        //取消掉所有协程内容
//        cancel()
        cancel()
    }
}
