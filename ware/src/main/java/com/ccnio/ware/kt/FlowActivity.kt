package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ccnio.ware.compose.ui.widget.SpanText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TAG = "FlowActivityL"

class FlowActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    private fun lifecycle() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

            }
        }
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
