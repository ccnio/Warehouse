package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ccnio.ware.compose.ui.widget.SpanText
import kotlinx.coroutines.launch

private const val TAG = "FlowActivityL"

class FlowActivity : ComponentActivity() {
//    private val binding by viewBinding(ActivityFlowBinding::bind)
//    private val viewModel by viewModels<FlowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            WarehouseTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(//https://juejin.cn/post/6861464723699957768
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
            Content()
//                }
//            }
        }
//        viewModel.stateFlow.observe(this) { Log.d(TAG_L, "stateFlow: $it") }
//        binding.flowView.setOnClickListener { flowUse() }
    }

//    private fun flowUse() {
//        viewModel.doTask()
//    }

    private fun lifecycle() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

            }
        }
    }

    @Composable
    fun Content() {
        Row {
            SpanText(text = "lifeCycle", Modifier.clickable {
                Log.d(TAG, "lifecycle")
                //https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn
            })
            SpanText(text = "stateFlow", Modifier.clickable {
                Log.d(TAG, "stateFlow")
                //https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn
            })
        }
    }

    @Composable
    @Preview
    fun PreviewContent() {
        Row {
            SpanText(text = "lifeCycle", Modifier.clickable {
                Log.d(TAG, "lifecycle")
                //https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn
            })
            SpanText(text = "stateFlow", Modifier.clickable {
                Log.d(TAG, "stateFlow")
                //https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn
            })
        }
    }
}
