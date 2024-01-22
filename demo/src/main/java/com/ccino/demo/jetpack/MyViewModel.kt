package com.ccino.demo.jetpack

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "MyViewModel"

class MyViewModel(private val saveStateHandle: SavedStateHandle) : ViewModel() {
    val info = saveStateHandle.get<String>("KEY_INFO") // 取值
    val infoFlow = saveStateHandle.getStateFlow("KEY_INFO", "") // 以 flow 形式

    fun setInfo(info: String) { // 设置值
        saveStateHandle.set("KEY_INFO", info)
    }
}