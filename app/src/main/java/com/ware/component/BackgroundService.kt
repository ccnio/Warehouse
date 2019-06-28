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
import com.ware.common.Utils

/**
 * Created by jianfeng.li on 19-6-14.
 */
val CHANNEL_ID = Utils.mContext.packageName + ".component"

/**
 * 8.0禁止常规方式从后台启动service, 需要startForegroundService(Intent(applicationContext, BackgroundService::class.java)) 并且创建一个通知
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
            val notification = Notification.Builder(applicationContext, CHANNEL_ID).build()
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