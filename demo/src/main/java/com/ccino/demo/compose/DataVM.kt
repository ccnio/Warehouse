package com.ccino.demo.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DataVM : ViewModel() {
    fun changeData() {
        val list = listOf(
            "Android2",
            "Kotlin2",
            "Compose2",
            "Jetpack",
            "Firebase",
            "AI/ML",
            "Web",
            "Cloud",
            "Security",
            "Testing",
        )
       topics.value = list
    }

    val topics by lazy {
        val topics = listOf(
            "Android",
            "Kotlin",
            "Compose",
            "Jetpack",
            "Firebase",
            "AI/ML",
            "Web",
            "Cloud",
            "Security",
            "Testing",
        )
        MutableStateFlow(topics)
    }
}