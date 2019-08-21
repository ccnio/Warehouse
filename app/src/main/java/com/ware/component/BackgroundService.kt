package com.ware.component

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.ware.R
import com.ware.common.Utils

/**
 * Created by jianfeng.li on 19-6-14.
 */
val CHANNEL_ID = Utils.mContext.packageName + ".component"

/**
 * 8.0从后台启动service
 * 1. 需要startForegroundService(Intent(applicationContext, BackgroundService::class.java))
 * 2. 需要Service里并且创建一个前台通知 startForeground(1, notification)
 * 3. 权限 <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
 */
class BackgroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("BackgroundService", "onCreate: ")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        /**
         * Bad notification for startForeground: invalid channel for service notification
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "component", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            val notification = Notification.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentTitle("content title")
                    .setContentText("content text")
                    .build()
            startForeground(1, notification)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BackgroundService", "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}