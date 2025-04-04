package com.ccino.demo

import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ccino.demo.media.audio.AudioRecorder
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioRecorderBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val audioRecorder = AudioRecorder()

    @Test
    fun benchmarkStartRecording() = runBlocking {
        benchmarkRule.measureRepeated {
            for (i in 0..1000) {
                val str = "a" + "b"
                Log.d("terst", "benchmarkStartRecording: $str")
            }

        }
    }
//
//    @Test
//    fun benchmarkStopRecording() = runBlocking {
//        audioRecorder.startRecording("benchmark_${System.currentTimeMillis()}.mp3")
//
//        benchmarkRule.measureRepeated {
//            audioRecorder.stopRecording()
//        }
//    }
//
//    @Test
//    fun benchmarkCancelRecording() = runBlocking {
//        audioRecorder.startRecording("benchmark_${System.currentTimeMillis()}.mp3")
//
//        benchmarkRule.measureRepeated {
//            audioRecorder.cancelRecording()
//        }
//    }
}