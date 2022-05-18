package com.ccnio.ware.perform

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccino.store.R

private const val TAG = "PerformActivity"

class PerformActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perform)
        Log.d(TAG, "onCreate : ${UsedClass().test()}")
    }
}