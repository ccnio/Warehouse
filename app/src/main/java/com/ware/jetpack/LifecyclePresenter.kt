package com.ware.jetpack

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.util.Log

/**
 * Lifecycle 是 Android Architecture Components 的一个组件，用于将系统组件（Activity、Fragment等等）的生命周期分离到 Lifecycle 类，
 * Lifecycle 允许其他类作为观察者，观察组件生命周期的变化。
 * Lifecycle 用起来很简单，首先声明一个 LifecycleObserver 对象，用 @OnLifecycleEvent 注解声明生命周期事件回调的方法：
 * 然后在 LifecycleRegistryOwner 比如 LifecycleActivity 加入这么一行代码：
 * getLifecycle().addObserver(new LifecycleObserverDemo());
 * 运行起来可以看到 LifecycleActivity 的生命周期发生变化时，LifecycleObserverDemo 总能得到通知。
 * 而 LifecycleActivity 只有寥寥几行代码，并没有覆盖任何回调方法。
 *
 *
 *
 * kotlin 相关
 * Kotlin
Kotlin extension modules are supported for several AndroidX dependencies labeled with // use -ktx for Kotlin below, just replace e.g.:

implementation "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version" // use -ktx for Kotlin
with

implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
More information, including docs for Kotlin extensions, can be found in the ktx documentation.

Note: For Kotlin based apps, make sure you use kapt instead of annotationProcessor. You should also add the kotlin-kapt plugin.

Lifecycle
Dependencies for Lifecycle, including LiveData and ViewModel.

def lifecycle_version = "1.1.1"

// ViewModel and LiveData
implementation "android.arch.lifecycle:extensions:$lifecycle_version"
// alternatively - just ViewModel
implementation "android.arch.lifecycle:viewmodel:$lifecycle_version" // use -ktx for Kotlin

 *
 */
class LifecyclePresenter(private val lifecycle: Lifecycle) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(TAG, "onAny : ${event.name}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d(TAG, "onCreate ")
        opeFunction("from onCreate")
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.d(TAG, "onResume")
        opeFunction("from onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "onDestroy ")
        opeFunction("from onDestroy")
    }

    private fun opeFunction(s: String) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "opeFunction $s") //from onResume
        }
    }

    companion object {
        const val TAG = "LifecyclePre"
    }
}