package com.ccino.demo.media.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.ccino.demo.media.audio.AudioRecordState.Stop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

private const val TAG = "AudioRecorder"

class AudioRecorder(private val config: AudioConfig = AudioConfig()) {
    private var recorder: MediaRecorder? = null
    private var startMills = 0L

    private val app = com.ccino.demo.app
    private val _recordState = MutableStateFlow<AudioRecordState>(AudioRecordState.Invalid)
    val recordState: StateFlow<AudioRecordState> = _recordState
    private var outputPath: File? = null

    fun start(outputPath: File) {
        if (app.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _recordState.value = AudioRecordState.Error()
            Log.e(TAG, "start: 未授权录音权限")
            return
        }

        try {
            this@AudioRecorder.outputPath = outputPath
            if (!outputPath.exists()) {
                val ret = outputPath.createNewFile()
                Log.d(TAG, "createNewFile: $ret")
            }
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(app)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioChannels(config.channels)
                config.bitRate?.let { setAudioEncodingBitRate(it) }
                config.samplingRate?.let { setAudioSamplingRate(it) }
                setOutputFile(outputPath)
                setMaxDuration(config.maxMs.toInt())
                setOnInfoListener { _, what, _ ->
                    when (what) {
                        MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> {
                            this@AudioRecorder.stop(maxDuration = true)
                        }
                    }
                }
            }

            recorder?.prepare()
            recorder?.start()
            startMills = SystemClock.elapsedRealtime()
            _recordState.value = AudioRecordState.Recording
        } catch (e: Exception) {
            Log.e(TAG, "start: catch=${e.message}")
            _recordState.value = AudioRecordState.Error()
        }
    }

    fun stop(maxDuration: Boolean = false) {
        try {
            recorder?.stop() // start、stop 之间的时间太短会导致 stop 抛异常
            recorder?.release()
            // 记录结束时间
            val cur = SystemClock.elapsedRealtime()
            _recordState.value = if (cur - startMills < config.minMs) {
                Log.i(TAG, "stop: too short")
                AudioRecordState.Error(tooShort = true)
            } else {
                Log.i(TAG, "stop: reachMax=$maxDuration")
                Stop(path = outputPath, maxDuration)
            }
        } catch (e: Exception) {
            Log.e(TAG, "stop: ex=${e.message}")
            _recordState.value = AudioRecordState.Error()
        }
    }

    fun release() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
            Log.e(TAG, "release: ex=${e.message}")
        }
    }
}