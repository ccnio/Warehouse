package com.ccino.ware.kt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.ware.common.BaseViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by jianfeng.li on 2021/8/24.
 */
class FlowViewModel : BaseViewModel() {
    private var count = 0
    val stateFlow = MutableStateFlow(count)
    val stateFlow2 = MutableStateFlow(count)
    val stateFlow3 = MutableStateFlow(count).asLiveData()

    /**
     * 1. 当 MutableSharedFlow 中缓存数据量超过阈值时，emit 方法和 tryEmit 方法的处理方式会有不同：
     * emit 方法：当缓存策略为 BufferOverflow.SUSPEND 时，emit 方法会挂起，直到有新的缓存空间。
     * tryEmit 方法：tryEmit 会返回一个 Boolean 值，true 代表传递成功，false 代表会产生一个回调，让这次数据发射挂起，直到有新的缓存空间。
     * 2. shareFlow 发射数据不是value而是emit
     * 3. 将冷流转化为SharedFlow, 直接使用官网的代码，方法是使用 Flow 的扩展方法 shareIn：
     */
    val sharedFlow = MutableSharedFlow<Int>(
        5 // 参数一：当新的订阅者Collect时，发送几个已经发送过的数据给它
        , 3 // 参数二：减去replay，MutableSharedFlow还缓存多少数据
        , BufferOverflow.DROP_OLDEST // 参数三：缓存策略，三种 丢掉最新值、丢掉最旧值和挂起
    )

    private suspend fun setShareFlow() {
        sharedFlow.emit(count)
    }

    val liveData = MutableLiveData(count)

    fun changeCount() {
        count = 5
        stateFlow.value = count
        liveData.value = count
    }
}