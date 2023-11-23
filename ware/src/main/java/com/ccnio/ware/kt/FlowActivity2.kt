package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityFlow2Binding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.concurrent.thread

private const val TAG = "FlowActivity2"

@AndroidEntryPoint
class FlowActivity2 : AppCompatActivity() {
    private val bind by viewBinding(ActivityFlow2Binding::bind)
    private val viewModel: FlowViewModel by viewModels()
    private val stateFlow = MutableStateFlow(0)
    private val eventFlow = MutableSharedFlow<Int>(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow2)
        bind.stateView.setOnClickListener { emitState() }
        bind.shareView.setOnClickListener { emitShare() }
        bind.shareCollectView.setOnClickListener { otherShareCollect() }
        bind.dataView.setOnClickListener { emitDataState() }

        lifecycleScope.launch {
            stateFlow.collect {
                Log.d(TAG, "onSizeChange: size=$it")
            }
        }

        lifecycleScope.launch {
            eventFlow.collect {
                Log.d(TAG, "onEvent: size=$it")
            }
        }

        lifecycleScope.launch {
            dataStateFlow.collect {
                Log.d(TAG, "dataState: size=$it")
            }
        }
    }

    private fun emitState() {

//        stateFlow.value = Date().seconds // or
          lifecycleScope.launch {
              stateFlow.emit(Date().minutes)
          }
    }

    private val user = User("li", listOf("ball"))
    private val dataStateFlow = MutableStateFlow(user)
    private fun emitDataState() {
//        dataStateFlow.value = User("li", listOf("ball"))
//        dataStateFlow.value = User("wang", listOf("ball"))

        user.name = "zhang"
        dataStateFlow.value = user
    }

    private fun emitShare() {
        lifecycleScope.launch {
            eventFlow.emit(Date().seconds)
        }
    }

    private fun otherShareCollect() {
        lifecycleScope.launch {
            eventFlow.collect {
                Log.d(TAG, "otherShareCollect: $it")
            }
        }
    }

    private val sharedCombineFlow1 = MutableSharedFlow<Int>()
    private val sharedCombineFlow2 = MutableSharedFlow<Int>()

    private fun combine() {
        lifecycleScope.launch {
            //f1、f2同时有数据，才会执行 combine 的【transform】函数，collect 才会执行
            sharedCombineFlow1.combine(sharedCombineFlow2) { a, b ->
                Log.d(TAG, "combine shared: n1 = $a, n2 = $b")
                a + b //返回值即发送的最终结果
            }.collect {
                Log.d(TAG, "combine shared: $it")
            }
        }

//        lifecycleScope.launch {
//            flow1.combine(flow2) { a, b ->
//                Log.d(TAG, "caseCombine: n1 = $a, n2 = $b")
//                a + b //返回值即发送的最终结果
//            }.distinctUntilChanged().collect {
//                Log.d(TAG, "caseCombine: $it")
//            }
//        }
    }

    private fun lifecycle() {
        viewModel.doTask() //开启后台循环任务

        //1. 处于onStop时依旧在接收，容易出现问题.如何在onStop时停止接收，可见时又接收？
//         lifecycleScope.launch { viewModel.stateFlow.collect { Log.d(TAG, "lifecycle: $it") } }
        lifecycleScope.launch {
            //[每次]可见时stateFlow 会收到最新的，sharedFlow数据发送时位于后台，再resume就接收不到了
            //flowWithLifecycle 内部调用的 repeatOnLifecycle
            /* viewModel.stateFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                 Log.d(TAG, "lifecycle: $it")
             }*/
            viewModel.sharedFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                Log.d(TAG, "lifecycle: $it")
            }

            /* //或者
             lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                 viewModel.stateFlow.collect {
                     Log.d(TAG, "lifecycle222: $it")
                 }
             }*/
        }
        //2.repeatOnLifecycle可以将活动限制在某个范围之内，但是这个会随着生命周期的反复来进行重复启动，
        //只使用一次的话可以使用launchWhenXXX，无论生命周期走几次，只会回调一次
        //即使早已 start/create，回调也会走一次
        lifecycleScope.launchWhenStarted { Log.d(TAG, "lifecycle: launchWhenStarted") }
        lifecycleScope.launchWhenCreated { Log.d(TAG, "lifecycle: launchWhenCreated") }
        val flow = flowOf(1, 2, 3, 4, 5).onEach {
            delay(2000)
            Log.d(TAG, "flow receive: $it")
        }
        //3.launchIn(Scope) 在指定的作用域里 terminal,
        lifecycleScope.launch { flow.launchIn(lifecycleScope) }//其实就是 mScope.launch { collect() }
        //4.stateIn()转换成 StateFlow,shareIn转换成SharedFlow
        lifecycleScope.launch { val stateFlow = flow.stateIn(lifecycleScope) }
    }
}