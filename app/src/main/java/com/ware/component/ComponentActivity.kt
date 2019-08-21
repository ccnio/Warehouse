package com.ware.component

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import kotlinx.android.synthetic.main.activity_component.*

class ComponentActivity : AppCompatActivity(), View.OnClickListener {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv1 -> {

                val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2 * 1000, "AlarmTest",
                        object : AlarmManager.OnAlarmListener {
                            override fun onAlarm() {
                                Log.d("ComponentActivity", "onAlarm: ")
                                applicationContext.startForegroundService(Intent(applicationContext, BackgroundService::class.java))
                            }
                        },
                        object : Handler() {
                            override fun handleMessage(msg: Message?) {
                                super.handleMessage(msg)
                                Log.d("AlarmWakeActivity", "handleMessage: ")
                            }
                        })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_component)
        tv1.setOnClickListener(this)
    }
}
