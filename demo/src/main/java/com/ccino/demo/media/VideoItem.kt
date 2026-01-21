package com.ccino.demo.media

/**
 * 视频数据模型
 */
data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val coverUrl: String? = null
)
