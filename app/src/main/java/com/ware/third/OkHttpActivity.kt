package com.ware.third

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_okhttp.*
import okio.*
import java.io.File

/**
 * Okio
 * 读取流Source,写入流Sink
 */
class OkHttpActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_okhttp)
        mReadView.setOnClickListener(this)
        mWriteView.setOnClickListener(this)
        Log.d("OkHttpActivity", "onCreate: ${Environment.getExternalStorageDirectory().absolutePath}")
    }

    private fun read(file: File) {
        //1.构建 Source
        val source: Source = Okio.source(file)
        //2.构建 BufferedSource
        val buffer: BufferedSource = Okio.buffer(source)
        while (true) {
            //3.按 utf8 的格式逐行读取字符
            val line = buffer.readUtf8Line()
            if (line != null) {
                Log.d("OkHttpActivity", "read: $line")
            } else {
                source.close()// kotlin use函数
                return
            }
        }

    }

    private fun write(file: File) {
        //Okio.appendingSink(file)
        val sink: Sink = Okio.sink(file)
        val buffer: BufferedSink = Okio.buffer(sink)
        buffer.writeUtf8("ab")
        buffer.writeUtf8("cd")
        buffer.close()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
}
