package com.ccino.demo.caster.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {
    var currentDate by mutableStateOf("")
        private set

    init {
        updateCurrentDate()
    }

    fun updateCurrentDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        currentDate = dateFormat.format(Date())
    }
}