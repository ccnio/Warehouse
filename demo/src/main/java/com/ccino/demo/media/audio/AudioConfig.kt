package com.ccino.demo.media.audio

import android.media.MediaMetadataRetriever
import java.io.File

data class AudioConfig(
    val maxMs: Long = 18_000, // 默认最大录音时长
    val minMs: Long = 4_000, // 默认最小录音时长
    val channels: Int = 1,
    val bitRate: Int? = null,
    val samplingRate: Int? = null
)

sealed class AudioRecordState {
    object Recording : AudioRecordState()
    class Stop(val path: File? = null, val maxDuration: Boolean = false) : AudioRecordState()
    data class Error(val tooShort: Boolean = false) : AudioRecordState()
    object Invalid : AudioRecordState()
}

fun getFileDuration(filePath: String): Long {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(filePath)

    // 获取时长，单位为毫秒
    val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    return duration?.toLong() ?: 0
}