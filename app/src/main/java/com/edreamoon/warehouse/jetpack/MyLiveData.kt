package com.edreamoon.warehouse.jetpack

import android.arch.lifecycle.LiveData
import android.util.Log
import java.util.*


/**
 * LiveData是一个用于持有数据并支持数据可被监听（观察）。
 * 和传统的观察者模式中的被观察者不一样，LiveData是一个生命周期感知组件，因此观察者可以指定某一个LifeCycle给LiveData，并对数据进行监听。
 * 如果观察者指定LifeCycle处于Started或者RESUMED状态，LiveData会将观察者视为活动状态，LiveData只通知处于活跃状态的observer,不活跃的不通知其改变。
 */
class MyLiveData : LiveData<LifeBean>() {


    /**
     * 这个方法被调用时，表示LiveData的观察者数量变为了0，既然没有了观察者，也就没有理由再做监听。
     * onStop/onDestroy 会调用
     */
    override fun onInactive() {
        super.onInactive()
        Log.d(TAG, "onInactive")
    }

    /**
     * 当这个方法被调用时，表示LiveData的观察者数量从0变为了1，这时就应该注册事件监听了。
     * onResume 会调用
     */
    override fun onActive() {
        super.onActive()
        Log.d(TAG, "onActive")
    }

    fun updateData() {
        val user = LifeBean("default")
        value = user

        // 初始化定时器
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                user.age = Random().nextInt() * 20
                user.mDesc = "${user.age}"
                postValue(user)
                Log.d(LifecycleActivity.TAG, "setValue: ${user.age}")
            }
        }, 2000, 2000)
    }

    companion object {
        const val TAG = "MyLiveData"
        var liveData: MyLiveData? = null
        fun getInstance(): MyLiveData? {
            if (liveData == null)
                liveData = MyLiveData()

            return liveData
        }
    }
}