package com.ccnio.ware.kt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by jianfeng.li on 2021/8/24.
 */
private const val TAG = "FlowViewModel"

class DataRet(var name: String? = null) {
    override fun toString(): String {
        return "DataRet(name=$name)"
    }
}

@HiltViewModel
class FlowViewModel @Inject constructor() : ViewModel() {
    val stateFlow = MutableStateFlow(-1)
    fun doTask() {
        viewModelScope.launch {
            delay(5000)
            stateFlow.emit(1)
            Log.d(TAG, "doTask:")
        }
    }
}