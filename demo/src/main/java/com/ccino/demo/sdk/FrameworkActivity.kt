package com.ccino.demo.sdk

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ccino.demo.R


private const val TAG = "FrameworkActivity"

class FrameworkActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    private fun getAttachedWindow(context: Context): Window? {
        if (context is Activity) {
            return context.window
        } else if (context is ContextThemeWrapper) {
            val baseContext: Context = context.baseContext
            return getAttachedWindow(baseContext)
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_framework)
        textView = findViewById(R.id.thread_view)
        textView.setOnClickListener { createViewInThread(this) }
    }

    override fun onResume() {
        super.onResume()
        updateUiViewInThread(textView)
    }
}