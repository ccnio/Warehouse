package com.ccnio.ware.kt

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.*

private const val TAG_L = "CoroutineActivity"

class CoroutineActivity : AppCompatActivity() {
    private val binding by viewBinding(com.ccnio.ware.databinding.ActivityCoroutineBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)
        binding.scopeView.setOnClickListener { scope() }
        binding.cancelView.setOnClickListener { cancel() }
    }

    private fun cancel() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch(Dispatchers.IO) {
            while (true) {
                delay(500) //如果用SystemClock则无法取消。 取消原理
//                SystemClock.sleep(500)
                Log.d(TAG_L, "scope: loop ${System.currentTimeMillis()}")
            }
        }
        binding.scopeView.postDelayed({ scope.cancel() }, 3000)
    }

    /**
     * 注意打印顺序
     */
    private fun scope() {
        val scope = CoroutineScope(Dispatchers.Main)
/*        scope.launch {
            launch {
                Log.d(TAG_L, "scope: sub launch")//3
            }
            Log.d(TAG_L, "scope: launch")//2
        }*/

        scope.launch {
            val async = async { Log.d(TAG_L, "scope: async") }//3
            //async.await() 放开此行时 1 2 3，关闭此行时 1 3 2
            Log.d(TAG_L, "scope: after async")//2
        }
        Log.d(TAG_L, "scope: ") //1
    }
}