package com.ware.kt

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_kt.*
import kotlinx.coroutines.*

class KtActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.mCoroutineModeView -> coroutineMode()
            R.id.mCoroutineDispatcherView -> coroutineDispatcher()
            R.id.mCoroutineAsyncView -> coroutineAsync()
        }
    }

    private fun coroutineAsync() {
        /**
         * 顺序执行
         * 01-20 20:51:34.745  7196  7196 D KtActivity: coroutineAsync async1: main
         * 01-20 20:51:36.757  7196  7196 D KtActivity: coroutineAsync async2: main
         * 01-20 20:51:36.760  7196  7196 D KtActivity: coroutineAsync async3: main
         */
        /* GlobalScope.launch(Dispatchers.Main) {
                Log.d("KtActivity", "coroutineAsync async1: ${Thread.currentThread().name}")
                val deferred = async {
                    delay(2000)
                    Log.d("KtActivity", "coroutineAsync async2: ${Thread.currentThread().name}")
                }
                deferred.await()
                Log.d("KtActivity", "coroutineAsync async3: ${Thread.currentThread().name}")
            }*/


        /**
         * 顺序执行
         * 01-20 20:54:37.933  7380  7380 D KtActivity: coroutineAsync: main
         * 01-20 20:54:39.956  7380  7439 D KtActivity: coroutineAsync1: DefaultDispatcher-worker-1
         * 01-20 20:54:39.964  7380  7439 D KtActivity: coroutineAsync2: DefaultDispatcher-worker-1
         * 01-20 20:54:39.966  7380  7380 D KtActivity: coroutineAsync3: main
         */
        /* GlobalScope.launch(Dispatchers.Main) {
             Log.d("KtActivity", "coroutineAsync: ${Thread.currentThread().name}")
             val async1 = async(Dispatchers.IO) {
                 delay(2000)
                 Log.d("KtActivity", "coroutineAsync1: ${Thread.currentThread().name}")
                 "async1"
             }.await()

             val async2 = async(Dispatchers.IO) {
                 Log.d("KtActivity", "coroutineAsync2: ${Thread.currentThread().name}")
                 "async2"
             }.await()

             Log.d("KtActivity", "coroutineAsync3: ${Thread.currentThread().name} ${async1 + async2}")
         }*/


        /**
         * 并行执行
         * 01-20 21:01:39.018  7768  7768 D KtActivity: coroutineAsync: main
         * 01-20 21:01:39.037  7768  7843 D KtActivity: coroutineAsync2: DefaultDispatcher-worker-2
         * 01-20 21:01:41.049  7768  7842 D KtActivity: coroutineAsync1: DefaultDispatcher-worker-1
         * 01-20 21:01:41.053  7768  7768 D KtActivity: coroutineAsync3: main async1  async2
         */
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("KtActivity", "coroutineAsync: ${Thread.currentThread().name}")
            val async1 = async(Dispatchers.IO) {
                delay(2000)
                Log.d("KtActivity", "coroutineAsync1: ${Thread.currentThread().name}")
                "async1"
            }

            val async2 = async(Dispatchers.IO) {
                Log.d("KtActivity", "coroutineAsync2: ${Thread.currentThread().name}")
                "async2"
            }

            Log.d("KtActivity", "coroutineAsync3: ${Thread.currentThread().name} ${async1.await()}  ${async2.await()}")
        }

    }

    /**
     * 调度
     */
    private fun coroutineDispatcher() {
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("KtActivity", "coroutineDispatcher Main: ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.Default) {
            Log.d("KtActivity", "coroutineDispatcher Default: ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("KtActivity", "coroutineDispatcher Main: ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.Unconfined) {
            Log.d("KtActivity", "coroutineDispatcher Main: ${Thread.currentThread().name}")//main
        }
    }

    /**
     * 启动模式
     */
    private fun coroutineMode() {
        //launch mode: DEFAULT,LAZY
        val jobDefault = GlobalScope.launch(start = CoroutineStart.DEFAULT) {
            Log.d("KtActivity", "coroutineLaunch default mode: ${Thread.currentThread().name}")
        }
//        jobDefault.cancel()

        val jobLazy = GlobalScope.launch(start = CoroutineStart.LAZY) {
            Log.d("KtActivity", "coroutineLaunch lazy mode: ${Thread.currentThread().name}")
        }
        jobLazy.start()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt)
        mCoroutineModeView.setOnClickListener(this)
        mCoroutineDispatcherView.setOnClickListener(this)
        mCoroutineAsyncView.setOnClickListener(this)
    }

}
