package com.ccnio.ware.sys

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivitySysApiBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding

private const val TAG = "SysApiActivity"

class SysApiActivity : AppCompatActivity(R.layout.activity_sys_api) {
    private val bind by viewBinding(ActivitySysApiBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.dndView.setOnClickListener { dnd() }
    }

    //勿扰模式
    private fun dnd() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val curFilter = manager.currentInterruptionFilter
        val notificationPolicy = manager.notificationPolicy
        Log.d(TAG, "dnd: filter = $curFilter, notifyPolicy = $notificationPolicy")
    }

}
