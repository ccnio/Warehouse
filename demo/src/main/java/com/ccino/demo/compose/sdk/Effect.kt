package com.ccino.demo.compose.sdk

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccino.demo.compose.ApiCaseVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun EffectCase(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        DisposeEffect()
        LaunchEffect(Modifier.padding(start = 10.dp))
        RememberScope(Modifier.padding(start = 10.dp))
        SideCase(Modifier.padding(start = 10.dp))
    }
}

private const val TAG_DISPOSE = "DisposeEffect"

/**
 * DisposableEffect 用来处理**需要清理的副作用（side effect）**的关键工具。
 * 它的核心作用可以概括为：在 Composable 进入界面时执行一个操作，并在它离开界面时执行一个清理操作。
 * 当 Composable 离开组合或键值变化时调用 onDispose
 * 1. 调用 context.registerReceiver(receiver, filter) 来注册这个接收器。这是一个典型的副作用，因为它与 Compose 的UI渲染无关，而是与外部的 Android 系统进行交互。
 * 2. 清理副作用（离开界面时）： 这个 BroadcastReceiver 如果不取消注册（unregister），就会造成内存泄漏。
 */
@SuppressLint("UnspecifiedRegisterReceiverFlag")
@Composable
fun DisposeEffect() {
    val context = LocalContext.current
    Log.d(TAG_DISPOSE, "Start")

    Row {
        Text(
            text = "Send",
            modifier = Modifier.clickable {
                val intent = Intent("mills_receiver")
                intent.putExtra("mills", System.currentTimeMillis())  // 例如播放次数为 5
                context.sendBroadcast(intent)
                Log.d(TAG_DISPOSE, "click")
            }
        )


        var playCount by remember { mutableLongStateOf(0L) }
        DisposableEffect(Unit) { // 重组时不执行
            Log.d(TAG_DISPOSE, "DisposableEffect")
            val filter = IntentFilter("mills_receiver")
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    val count = intent?.getLongExtra("mills", 0L) ?: 0L
                    playCount = count     // 更新 compose UI
                }
            }

            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED )
            onDispose { // 等价于 onDestroy
                Log.d(TAG_DISPOSE, "DisposeEffect: onDispose")
                context.unregisterReceiver(receiver)
            }
        }
        Text(text = playCount.toString(), modifier = Modifier.padding(start = 5.dp))
    }
}


/**
 * LaunchedEffect
 *  当 Composable 进入界面时，在一个与 Composable 生命周期绑定的协程作用域 (CoroutineScope) 中启动一个 suspend 函数。
 *  它提供了一个协程环境，所以你可以在里面安全地调用挂起函数，比如网络请求、数据库操作、显示 Snackbar 等。
 *  ◦它有一个或多个 key 参数。当 key 发生变化时，它会取消上一个协程并启动一个新的协程。如果 key 保持不变，协程会一直运行。
 *  如果传入 Unit 作为 key，则它只会在 Composable 首次进入界面时执行一次。◦当 Composable 离开界面时，协程会被自动取消，帮你避免了内存泄漏。
 */
@Composable
fun LaunchEffect(padding: Modifier) {
    Log.d("LaunchEffect", "start")
    var count by remember { mutableIntStateOf(0) }
    // key 传 unit的话，只在组合进入界面时执行一次，后续click 不会触发
    // key 传 count 的话，每次 count 变化时都会执行
    LaunchedEffect(count) {
        delay(1000L)
        Log.d("LaunchEffect", "LaunchEffect")
    }
    Button(onClick = {
        Log.d("LaunchEffect", "click")
        count++
    }, modifier = padding) {
        Text(text = "$count")
    }
}


/**
 * rememberCoroutineScope
 * 这个不是一个 Effect Composable，但它与副作用处理密切相关。•一句话总结： 获取一个与 Composable 生命周期绑定的协程作用域，但由你手动控制何时启动协程。
 * •主要特点和用途：◦与 LaunchedEffect 不同，它不会自动启动协程。它只是给你一个 scope，让你可以在用户交互事件（如点击事件）中启动协程。
 * ◦当 Composable 离开界面时，这个 scope 会被自动取消，其中正在运行的协程也会被取消。
 */
@Composable
fun RememberScope(modifier: Modifier = Modifier) {
    Log.d("RememberScope", "start")
    val scope = rememberCoroutineScope()
    Button(modifier = modifier, onClick = {
        scope.launch {
            delay(3000L)
            Log.d("RememberScope", "Job")
        }
    }) {
        Text(text = "Scope")
    }
}

/**
 * SideEffect
 * 在**每次** Composable 成功重组（recomposition）**之后**执行一段非 Compose 代码。
 * 它的执行时机非常频繁，只要 Composable 发生重组就会运行。
 * 主要用于将 Compose 的 `State` 同步到非 Compose 管理的对象上（比如一些传统的 View、第三方库或分析日志）。这被认为是一个“逃生舱口”。
 */
@Composable
fun SideCase(modifier: Modifier = Modifier, vm: ApiCaseVM = viewModel()) {
    val name by vm.nameFlow.collectAsState()

    Log.d("SideCase", "start")
    SideEffect { // 注意打印顺序 start、after、SideEffect
        // 这里的代码会在每次成功重组后执行
        Log.d("SideCase", "SideEffect: current name is '$name'")
        // 真实场景可能是这样: AnalyticsManager.logPropertyValue("current_name", name)
    }
    Log.d("SideCase", "after")

    TextField(
        modifier = modifier,
        value = name,
        onValueChange = { vm.updateName(it) },
    )
}
