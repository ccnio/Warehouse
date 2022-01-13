package com.ccnio.ware.jetpack.architecture

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by ccino on 2022/1/13.
 */
private const val TAG_L = "ArchitectureVM"

data class Result(val value: String)
class ArchitectureVM : ViewModel() {
    val _states = MutableSharedFlow<String>()
    val states = _states.asLiveData()

    /**
     * LiveData 有默认值时，一上来就会发送出去。
     */
    val liveData = MutableLiveData("liveData default")
    val liveDataTransform = MutableLiveData<String>()

    fun changeStates() {
        liveData.value = "liveData"
        viewModelScope.launch { _states.emit("states") }
        liveDataTransform.value = "liveTransform"
    }

    fun reqData() {
        viewModelScope.launch {
            flow {
                delay(2000)
                emit("result")
            }.onStart {
                Log.d(TAG_L, "reqData: start")
            }.catch {
                Log.d(TAG_L, "reqData: ${it.message}")
            }.collect {
                _states.emit(it)
            }
        }
    }
}