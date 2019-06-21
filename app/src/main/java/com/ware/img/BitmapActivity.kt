package com.ware.img

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.common.Utils

class BitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)

        val res = resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.bit)
        val byteCount = bitmap.byteCount
        Log.e("lijf", ": $byteCount")

        val bitmapHeight = bitmap.height
        val bitmapWidth = bitmap.width
        val bitmapConfig = bitmap.config.toString()
        Log.e("lijf", "bitmapHeight = $bitmapHeight;  bitmapWidth = $bitmapWidth; config = $bitmapConfig")
        //打印结果：bitmapHeight = 700;  bitmapWidth = 481; config = ARGB_8888

        Log.e("lijf", "density = " + Utils.getDensity())
        //图片内存占用 计算来源

    }

    companion object {

        private val TAG = "BitmapActivity"
    }
}
