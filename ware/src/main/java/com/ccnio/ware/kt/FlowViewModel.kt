package com.ccnio.ware.kt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by jianfeng.li on 2021/8/24.
 */
class DataRet(var name: String? = null) {
    override fun toString(): String {
        return "DataRet(name=$name)"
    }
}

private const val TAG_L = "FlowViewModel"

class FlowViewModel : ViewModel() {
    private val data = DataRet()
    private val _stateFlow = MutableSharedFlow<DataRet>()
    val stateFlow = _stateFlow.asLiveData()

    fun doTask() {
        viewModelScope.launch {
            flow {
                delay(2000)
                data.name = "result"
                emit(data)
            }.onStart {
                data.name = "start"
                _stateFlow.emit(data)
            }.catch {
                Log.d(TAG_L, "doTask: ")
            }.collect {
                _stateFlow.emit(it)
            }
        }
    }
}