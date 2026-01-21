package com.ccino.demo.dialog.chain

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import java.util.PriorityQueue

/**
 * 弹窗链管理器
 * 建议在 Activity 或 Fragment 中创建局部实例，避免全局单例导致的状态冲突。
 */
class DialogChainController : DefaultLifecycleObserver {

    private val taskQueue = PriorityQueue<DialogTask>()
    private var hostRef: WeakReference<LifecycleOwner>? = null
    private var lifecycleOwnerRef: WeakReference<LifecycleOwner>? = null

    private var isRunning = false
    private var isResumed = false

    /**
     * 绑定 Fragment (自动使用 viewLifecycleOwner)
     */
    fun attach(fragment: Fragment) {
        detach()
        this.hostRef = WeakReference(fragment)
        val viewOwner = fragment.viewLifecycleOwner
        this.lifecycleOwnerRef = WeakReference(viewOwner)
        viewOwner.lifecycle.addObserver(this)
    }

    /**
     * 绑定 Activity
     */
    fun attach(activity: ComponentActivity) {
        detach()
        this.hostRef = WeakReference(activity)
        this.lifecycleOwnerRef = WeakReference(activity)
        activity.lifecycle.addObserver(this)
    }

    fun detach() {
        lifecycleOwnerRef?.get()?.lifecycle?.removeObserver(this)
        lifecycleOwnerRef = null
        hostRef = null
        clear()
    }

    fun addTask(task: DialogTask): DialogChainController {
        taskQueue.offer(task)
        return this
    }

    fun start() {
        if (isRunning) return
        processNext()
    }

    private fun processNext() {
        val host = hostRef?.get()
        if (host == null) {
            clear()
            return
        }

        val activity = getActivity(host)
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            clear()
            return
        }

        if (host is Fragment) {
            if (!host.isAdded || host.isDetached || host.view == null) {
                isRunning = false
                return
            }
        }

        if (!isResumed) {
            isRunning = false
            return
        }

        val task = taskQueue.poll()
        if (task == null) {
            isRunning = false
            return
        }

        isRunning = true

        task.prepare(host, activity) { shouldShow ->

            // 再次检查环境
            val currentHost = hostRef?.get()
            val currentActivity = if (currentHost != null) getActivity(currentHost) else null

            val isFragmentValid = if (currentHost is Fragment) {
                currentHost.isAdded && !currentHost.isDetached && currentHost.view != null
            } else true

            if (shouldShow && isResumed && currentActivity != null && !currentActivity.isFinishing && isFragmentValid) {
                try {
                    task.show(currentHost!!, currentActivity, object : ChainCallback {
                        override fun next() {
                            processNext()
                        }

                        override fun stop() {
                            clear()
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    processNext()
                }
            } else {
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
        taskQueue.clear()
        isRunning = false
    }

    // --- Lifecycle Callbacks ---

    override fun onResume(owner: LifecycleOwner) {
        isResumed = true
        if (taskQueue.isNotEmpty() && !isRunning) {
            processNext()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        isResumed = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        if (lifecycleOwnerRef?.get() == owner) {
            lifecycleOwnerRef = null
            hostRef = null
            clear()
        }
    }
}
