package com.ccino.ware.libs

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityOkhttpBinding
import com.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.IOException

/**
 * Okio
 * 读取流Source,写入流Sink
 */
private const val TAG = "OkHttpActivityL"

class OkHttpActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope by MainScope() {
    private val binding by viewBinding(ActivityOkhttpBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_okhttp)
        binding.httpView.setOnClickListener(this)
        binding.mReadView.setOnClickListener(this)
        binding.mWriteView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.httpView -> reqHttp()
            R.id.mReadView -> {
                val file = File(Environment.getExternalStorageDirectory().absolutePath + "/a.txt")
                read(file)
            }
            R.id.mWriteView -> {
                val file = File(Environment.getExternalStorageDirectory().absolutePath + "/a.txt")
                write(file)
            }
        }
    }

    private fun reqHttp() {
        val httpClient = OkHttpClient()
        val request = Request.Builder().url("https://raw.github.com/square/okhttp/master/README.md").build()

        //同步请求
        launch {
            withContext(Dispatchers.IO) {
                httpClient.newCall(request).execute().use {
                    Log.d(TAG, "reqHttp: res = ${it.body?.string()}")
                }
            }
        }

        //异步请求
        httpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: ")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse: ${response.body?.string()}")
            }
        })

    }

    private fun read(file: File) {
//        //1.构建 Source
//        val source: Source = source(file)
//        //2.构建 BufferedSource
//        val buffer: BufferedSource = Okio.buffer(source)
//        while (true) {
//            //3.按 utf8 的格式逐行读取字符
//            val line = buffer.readUtf8Line()
//            if (line != null) {
//                Log.d("OkHttpActivity", "read: $line")
//            } else {
//                source.close()// kotlin use函数
//                return
//            }
//        }

    }

    private fun write(file: File) {
//        //Okio.appendingSink(file)
//        val sink: Sink = Okio.sink(file)
//        val buffer: BufferedSink = Okio.buffer(sink)
//        buffer.writeUtf8("ab")
//        buffer.writeUtf8("cd")
//        buffer.close()
    }
}
