package com.edreamoon.warehouse.kt

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.edreamoon.getS
import com.edreamoon.warehouse.R

class KtActivity : AppCompatActivity() {

    companion object {
        const val TAG:String = "KtActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt)
//        val s = getS()

        applicationContext.getS()
//        Log.d(TAG, s)

    }
}
