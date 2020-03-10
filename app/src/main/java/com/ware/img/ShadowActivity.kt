package com.ware.img

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ware.R


class ShadowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadow)

        Log.e("TAG", "")

    }

    companion object {
        private const val TAG = "ShadowActivity"
    }
}
