package com.ware.util

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import com.ware.R

private const val TAG = "PersistActivity"

class PersistActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootDir = MMKV.initialize(this)
        Log.d(TAG, "onCreate: dir = $rootDir")
        setContentView(R.layout.activity_persist)
    }
}