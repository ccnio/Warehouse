package com.ccnio.ware.jetpack.nav

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "ThirdViewModel"

class ThirdViewModel : ViewModel() {
    val liveData by lazy { MutableLiveData<Int>() }
    private var num = 0
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

    fun addLiveData() {
        num++
        liveData.value = num
    }
}