package com.ware.jetpack

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

private const val TAG = "CycleRuntimePresenter"

class CycleRuntimePresenter(val lifecycle: Lifecycle) : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "onCreate: ")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "onStart: ")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: ")
        opeFunction(" from onResume")
    }


    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "onDestroy: ")
    }

    private fun opeFunction(s: String) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(CycleCompilePresenter.TAG, "opeFunction $s")
        }
    }
}