package com.ccino.demo.media

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityRecorderBinding
import com.ccino.demo.media.audio.AudioRecorder
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "RecordActivity"

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecorderBinding
    private val recorder = AudioRecorder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.record.setOnClickListener { record() }
        binding.stop.setOnClickListener { stop() }
        lifecycleScope.launch { recorder.recordState.collect { Log.d(TAG, "onCreate: $it") } }
    }

    private fun record() {
        // 检查录音权限和写入存储权限
        val permissions = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )



//        if (permissionsToRequest.isNotEmpty()) {
        // 如果有未授权的权限，请求权限
        requestPermissions(permissions, 1001)
////            return
////        }
        val file = File(getExternalFilesDir(null), "recording.aac")
        val createNewFile = file.createNewFile()
//        Log.d(TAG, "record: path=${file.absolutePath}, $createNewFile, grant=$audioGrant")
        recorder.start(file)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $requestCode, ${permissions.toList()}, ${grantResults.toList()}")
    }

    private fun stop() {
        recorder.stop()
    }

}