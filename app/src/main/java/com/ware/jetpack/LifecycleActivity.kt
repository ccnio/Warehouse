package com.ware.jetpack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ware.R

/**
 * Lifecycle 用于将系统组件（Activity、Fragment等等）的生命周期分离到 Lifecycle 类，允许其他类作为观察者来观察组件生命周期的变化。
 *
 * 定义观察者
 * Observer的实现有两种方式：编译期处理/运行时处理
 * 1. Observer 实现 LifecycleObserver,用 @OnLifecycleEvent 注解声明生命周期事件回调的方法 -CycleCompilePresenter
 * 2. Observer 实现 DefaultLifecycleObserver, 覆盖对应生命周期事件方法即可 -CycleRuntimePresenter(prefer)
 *
 * 注册观察者
 * 定义好Observer后在 LifecycleRegistryOwner 比如 LifecycleActivity 加入观察者：getLifecycle().addObserver(new LifecycleObserverDemo());
 *
 * note
 * Observer会收到其注册时生命周期之前的回调。如在onStart()注册时，会立即收到onCreate的回调。参见源码
 */
class LifecycleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)

//        lifecycle.addObserver(CycleCompilePresenter(lifecycle))
    }

    override fun onStart() {
        super.onStart()
        lifecycle.addObserver(CycleRuntimePresenter(lifecycle))
    }

    companion object {
        const val TAG = "LifecycleActivity"
    }
}
