package com.ccino.ware.jetpack

import android.util.Log
import androidx.lifecycle.MutableLiveData

/**
 * Created by ccino on 2021/10/23.
 */
private const val TAG_L = "MyLiveData"

class MyLiveData : MutableLiveData<String>() {
    override fun onActive() {
        Log.d(TAG_L, "onActive: ")
    }

    override fun onInactive() {
        Log.d(TAG_L, "onInactive: ")
    }
}