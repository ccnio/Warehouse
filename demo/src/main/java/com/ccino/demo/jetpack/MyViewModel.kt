package com.ccino.demo.jetpack

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

private const val TAG = "MyViewModel"

class MyViewModel : ViewModel() {
    val event by lazy { MutableSharedFlow<String>() }

}