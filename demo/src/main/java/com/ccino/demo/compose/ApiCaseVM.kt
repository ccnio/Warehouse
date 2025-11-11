package com.ccino.demo.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiCaseVM : ViewModel() {
    private val _name = MutableStateFlow("init")
    val nameFlow = MutableStateFlow("init")

    fun updateName(newName: String) {
        nameFlow.value = newName
    }
}