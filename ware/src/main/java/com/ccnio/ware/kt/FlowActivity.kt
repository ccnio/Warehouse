package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ccnio.ware.R
import com.ccnio.ware.compose.ui.widget.SpanText
import com.ccnio.ware.databinding.ActivityFlowBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "FlowActivityL"

@AndroidEntryPoint
class FlowActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityFlowBinding::bind)
    private val viewModel: FlowViewModel by viewModels()
    private var a = 0
    private var b = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)
        bind.lifecycleView.setOnClickListener { lifecycle() }//生命周期相关
        bind.combineView.setOnClickListener { combine() }//combine操作符
        bind.mergeView.setOnClickListener { merge() }//
        bind.retryView.setOnClickListener { retry() }//
        bind.aView.setOnClickListener {
            a++
            lifecycleScope.launch { sharedCombineFlow1.emit(a) }
        }
        bind.bView.setOnClickListener {
            b++
            lifecycleScope.launch { sharedCombineFlow2.emit(b) }
        }
    }

    class RetryEx : Exception()

    private fun retry() {
        flowOf(1, 2, 3)
            .retryWhen { cause, attempt ->
                Log.d(TAG, "retry: $cause, attempt=$attempt")
                cause is RetryEx
            }
    }

    private val flow1 = MutableStateFlow("flow1")
    private val flow2 = MutableStateFlow("flow2")
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

    private fun merge() {
        val ints: Flow<Int> = flowOf(1, 2, 3)
            .onEach { delay(1000) }
        val doubles: Flow<Double> = flowOf(0.1, 0.2, 0.3)

        val together: Flow<Number> = merge(ints, doubles)
        lifecycleScope.launch {
            together.collect { Log.d(TAG, "merge: $it") }
        }
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
        /*
                //2.repeatOnLifecycle可以将活动限制在某个范围之内，但是这个会随着生命周期的反复来进行重复启动，
                //只使用一次的话可以使用launchWhenXXX，无论生命周期走几次，只会回调一次
                //即使早已 start/create，回调也会走一次
                lifecycleScope.launchWhenStarted { Log.d(TAG, "lifecycle: launchWhenStarted") }
                lifecycleScope.launchWhenCreated { Log.d(TAG, "lifecycle: launchWhenCreated") }*/
        val flow = flowOf(1, 2, 3, 4, 5).onEach {
            delay(2000)
            Log.d(TAG, "flow receive: $it")
        }
        //3.launchIn(Scope) 在指定的作用域里 terminal,
        lifecycleScope.launch { flow.launchIn(lifecycleScope) }//其实就是 mScope.launch { collect() }
        //4.stateIn()转换成 StateFlow,shareIn转换成SharedFlow
        lifecycleScope.launch { val stateFlow = flow.stateIn(lifecycleScope) }
    }

    @Composable
    fun Content() {
        Row(Modifier.padding(top = 25.dp)) {
            SpanText(text = "lifeCycle", Modifier.clickable {
                Log.d(TAG, "lifecycle")
                //https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn
            })
            SpanText(text = "发送Flow数据", Modifier.clickable { sendFlowData() })
            SpanText(text = "StateFlow", Modifier.clickable { stateFlow() })
            SpanText(text = "SharedFlow", Modifier.clickable { sharedFlow() })
            SpanText(
                text = "Combine1",
                Modifier.clickable { lifecycleScope.launch { flow1.emit("flow1") } })
            SpanText(
                text = "Combine2",
                Modifier.clickable { lifecycleScope.launch { flow2.emit("flow2") } })
        }
    }


    private fun sendFlowData() {
        //SharedFlow 多次发送同一对象还是会发送出去
//        lifecycleScope.launch { sharedFlow.emit(user) }
        lifecycleScope.launch { sharedFlow.emit(User2("mm", 12)) }

    }

    private val sharedFlow = MutableSharedFlow<User2>()
    private val user = User2("mm", 12)

    /**
     * SharedFlow
     * 1. 多次发送同一对象还是会发送出去
     * 2. sharedFlow.distinctUntilChanged() 可以达到与 stateFlow 效果一致
     */
    private fun sharedFlow() {
        lifecycleScope.launch {
            flowOf(1, 1, 1, 3, 3).distinctUntilChanged().collect {
                Log.d(TAG, "sharedFlow: $it")
            }
        }
//        lifecycleScope.launch { sharedFlow.collect { Log.d(TAG, "sharedFlow collect: $it") } }
        lifecycleScope.launch {
            sharedFlow.distinctUntilChanged()
                .collect { Log.d(TAG, "sharedFlow distinctUntilChanged2: $it") }
        }
    }

    private fun stateFlow() {

    }

}
