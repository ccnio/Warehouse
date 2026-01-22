package com.ccino.demo.dialog.chain

import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 链条回调接口，支持继续或中断
 */
interface ChainCallback {
    fun next()
    fun stop()
}

/**
 * 弹窗任务基类
 * @param priority 优先级，数值越大优先级越高
 * @param id 唯一标识，用于去重。空字符串表示不去重
 */
abstract class DialogTask(
    val priority: Int,
    val id: String = ""
) : Comparable<DialogTask> {

    /**
     * 1. 异步准备阶段
     * 用于异步请求数据、检查条件等
     * @param callback 回调，传入 true 表示需要显示，false 表示跳过
     */
    open fun prepare(host: LifecycleOwner, activity: ComponentActivity, callback: (Boolean) -> Unit) {
        callback(shouldShow(host, activity))
    }

    /**
     * 同步判断是否需要显示（默认实现）
     */
    protected open fun shouldShow(host: LifecycleOwner, activity: ComponentActivity): Boolean = true

    /**
     * 2. 显示阶段 (Template Method Pattern)
     */
    fun show(host: LifecycleOwner, activity: ComponentActivity, callback: ChainCallback) {
        val hasCalled = AtomicBoolean(false)
        
        val safeNext = {
            if (hasCalled.compareAndSet(false, true)) {
                callback.next()
            }
        }
        
        val safeStop = {
            if (hasCalled.compareAndSet(false, true)) {
                callback.stop()
            }
        }

        val safeChain = object : ChainCallback {
            override fun next() = safeNext()
            override fun stop() = safeStop()
        }

        onShow(host, activity, safeChain)
    }

    /**
     * 子类必须实现的具体显示逻辑
     * @param chain 链条控制器。调用 chain.next() 继续下一个；调用 chain.stop() 中断后续所有任务。
     */
    protected abstract fun onShow(host: LifecycleOwner, activity: ComponentActivity, chain: ChainCallback)

    override fun compareTo(other: DialogTask): Int {
        return other.priority - this.priority
    }
}
