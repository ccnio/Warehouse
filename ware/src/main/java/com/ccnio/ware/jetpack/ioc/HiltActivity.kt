package com.ccnio.ware.jetpack.ioc

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityHiltBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HiltActivity"

@AndroidEntryPoint
class HiltActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityHiltBinding::bind)

    //context ioc
    @Inject lateinit var contextApp: ContextApp
    @Inject lateinit var contextActivity: ContextActivity
    @Inject lateinit var contextActivityScope: ContextActivityScope
    @Inject lateinit var contextApp2: ContextApp2
    @Inject lateinit var contextActivity2: ContextActivity2
    @Inject lateinit var contextApp3: ContextApp3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilt)
        bind.contextView.setOnClickListener { contextDemo() }
    }

    private fun contextDemo() {
        Log.d(TAG, "contextDemo: this = $this, app = $application")
        Log.d(TAG, "contextDemo: contextActivity = ${contextActivity.activity}, contextApp = ${contextApp.application}")
        Log.d(TAG, "contextDemo2: contextActivity = ${contextActivity2.activity}, contextApp = ${contextApp2.application}")
        Log.d(TAG, "contextDemo3: contextApp = ${contextApp3.application}")
    }
}