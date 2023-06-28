package com.ccnio.ware.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityMediaBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MediaActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMediaBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        binding.bitMemView.setOnClickListener { sysApi() }
    }

    private val bits = mutableListOf<Bitmap>()
    private fun sysApi() {
        for (i in 0..50) {
            val width = 2000
            val height = 2000
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.RED)
            binding.imgView.setImageBitmap(bitmap)
        }
    }
}