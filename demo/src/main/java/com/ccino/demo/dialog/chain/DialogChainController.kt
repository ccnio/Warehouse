package com.ccino.demo.dialog.chain

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.PriorityQueue

/**
 * 弹窗链管理器
 * 建议在 Activity 或 Fragment 中创建局部实例，避免全局单例导致的状态冲突。
 * 
 * 优化版本：
 * - 简化生命周期管理
 * - 添加超时机制
 * - 添加任务去重
 * - 完善状态管理
 */
class DialogChainController : DefaultLifecycleObserver {
    
    companion object {
        private const val TAG = "DialogChainController"
        private const val PREPARE_TIMEOUT_MS = 30_000L // 30秒超时
    }
    
    // 状态枚举
    private enum class State {
        IDLE,       // 空闲
        PREPARING,  // 准备中（异步prepare阶段）
        SHOWING     // 显示中
    }

    private val taskQueue = PriorityQueue<DialogTask>()
    private val taskIds = mutableSetOf<String>() // 任务去重
    private var host: WeakReference<LifecycleOwner>? = null

    private var state = State.IDLE
    private var isResumed = false
    private var currentTimeoutJob: Job? = null
    
    // 协程作用域，用于超时处理
    private val scope = CoroutineScope(Dispatchers.Main)

    /**
     * 绑定 Fragment (自动使用 viewLifecycleOwner)
     */
    fun attach(fragment: Fragment) {
        Log.d(TAG, "attach Fragment: ${fragment.javaClass.simpleName}")
        detach()
        val viewOwner = fragment.viewLifecycleOwner
        this.host = WeakReference(viewOwner)
        viewOwner.lifecycle.addObserver(this)
    }

    /**
     * 绑定 Activity
     */
    fun attach(activity: ComponentActivity) {
        Log.d(TAG, "attach Activity: ${activity.javaClass.simpleName}")
        detach()
        this.host = WeakReference(activity)
        activity.lifecycle.addObserver(this)
    }

    fun detach() {
        Log.d(TAG, "detach")
        host?.get()?.lifecycle?.removeObserver(this)
        host = null
        clear()
        scope.cancel()
    }

    /**
     * 添加任务（支持去重）
     */
    fun addTask(task: DialogTask): DialogChainController {
        // 任务去重检查
        if (task.id.isNotEmpty()) {
            if (!taskIds.add(task.id)) {
                Log.w(TAG, "Task ${task.id} already added, skip")
                return this
            }
        }
        
        taskQueue.offer(task)
        Log.d(TAG, "addTask: ${task.javaClass.simpleName}, priority=${task.priority}, id=${task.id}, queue size=${taskQueue.size}")
        return this
    }
    
    /**
     * 移除指定任务
     */
    fun removeTask(taskId: String): DialogChainController {
        if (taskId.isEmpty()) return this
        
        val removed = taskQueue.removeAll { it.id == taskId }
        taskIds.remove(taskId)
        Log.d(TAG, "removeTask: $taskId, removed=$removed")
        return this
    }

    fun start() {
        Log.d(TAG, "start: state=$state, queue size=${taskQueue.size}")
        if (state != State.IDLE) {
            Log.w(TAG, "Already running, state=$state")
            return
        }
        processNext()
    }

    private fun processNext() {
        Log.d(TAG, "processNext: state=$state, resumed=$isResumed, queue=${taskQueue.size}")
        
        // 检查宿主是否有效
        val currentHost = host?.get()
        if (currentHost == null) {
            Log.w(TAG, "Host is null, clear queue")
            clear()
            return
        }

        val activity = getActivity(currentHost)
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            Log.w(TAG, "Activity is invalid, clear queue")
            clear()
            return
        }

        // Fragment 额外检查
        if (currentHost is Fragment) {
            if (!currentHost.isAdded || currentHost.isDetached || currentHost.view == null) {
                Log.w(TAG, "Fragment is invalid, pause")
                state = State.IDLE
                return
            }
        }

        // 必须在 Resumed 状态
        if (!isResumed) {
            Log.d(TAG, "Not resumed, pause")
            state = State.IDLE
            return
        }

        // 取出下一个任务
        val task = taskQueue.poll()
        if (task == null) {
            Log.d(TAG, "Queue is empty, done")
            state = State.IDLE
            taskIds.clear() // 清空去重记录
            return
        }
        
        // 从去重集合中移除（已经开始执行）
        taskIds.remove(task.id)

        // 进入准备阶段
        state = State.PREPARING
        Log.d(TAG, "Start preparing task: ${task.javaClass.simpleName}, id=${task.id}")

        // 启动超时监控
        currentTimeoutJob = scope.launch {
            delay(PREPARE_TIMEOUT_MS)
            Log.e(TAG, "Task prepare timeout: ${task.javaClass.simpleName}, skip to next")
            state = State.IDLE
            processNext()
        }

        task.prepare(currentHost, activity) { shouldShow ->
            // 取消超时
            currentTimeoutJob?.cancel()
            currentTimeoutJob = null
            
            Log.d(TAG, "Task prepare done: shouldShow=$shouldShow")

            // 再次检查环境（prepare 是异步的，可能已经变化）
            val recheckHost = host?.get()
            val recheckActivity = if (recheckHost != null) getActivity(recheckHost) else null

            val isFragmentValid = if (recheckHost is Fragment) {
                recheckHost.isAdded && !recheckHost.isDetached && recheckHost.view != null
            } else true

            if (shouldShow && isResumed && recheckActivity != null && !recheckActivity.isFinishing && isFragmentValid) {
                // 进入显示阶段
                state = State.SHOWING
                Log.d(TAG, "Start showing task: ${task.javaClass.simpleName}")
                
                try {
                    task.show(recheckHost!!, recheckActivity, object : ChainCallback {
                        override fun next() {
                            Log.d(TAG, "Task next: ${task.javaClass.simpleName}")
                            state = State.IDLE
                            processNext()
                        }

                        override fun stop() {
                            Log.d(TAG, "Task stop: ${task.javaClass.simpleName}")
                            clear()
                        }
                    })
                } catch (e: Exception) {
                    Log.e(TAG, "Task show error: ${task.javaClass.simpleName}", e)
                    state = State.IDLE
                    processNext()
                }
            } else {
                Log.w(TAG, "Skip task due to invalid state")
                state = State.IDLE
                processNext()
            }
        }
    }

    private fun getActivity(owner: LifecycleOwner): ComponentActivity? {
        return when (owner) {
            is ComponentActivity -> owner
            is Fragment -> owner.activity
            else -> null
        }
    }

    fun clear() {
        Log.d(TAG, "clear: queue size=${taskQueue.size}")
        currentTimeoutJob?.cancel()
        currentTimeoutJob = null
        taskQueue.clear()
        taskIds.clear()
        state = State.IDLE
    }
    
    /**
     * 获取当前队列状态（用于调试）
     */
    fun getStatus(): String {
        return "State=$state, Resumed=$isResumed, Queue=${taskQueue.size}, Tasks=${taskIds.size}"
    }

    // --- Lifecycle Callbacks ---

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: state=$state")
        isResumed = true
        // 只有在真正空闲时才恢复执行
        if (taskQueue.isNotEmpty() && state == State.IDLE) {
            processNext()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "onPause")
        isResumed = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "onDestroy")
        owner.lifecycle.removeObserver(this)
        if (host?.get() == owner) {
            host = null
            clear()
            scope.cancel()
        }
    }
}
