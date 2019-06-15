package com.ware.component

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * Created by jianfeng.li on 19-6-14.
 */
class BackgroundService : Service() {
    val CHANNEL_ID = "com.xiaomi.wear.deskclock.alarm"

    override fun onCreate() {
        super.onCreate()
        Log.d("BackgroundService", "onCreate: ")

//        val mChannel = NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(mChannel)

        //need user  Intent intent = new Intent(view.getContext(), BackgroundService.class);
        //                startForegroundService(intent); from background start service and must give follows code

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentIntent(pendingShowAlarmIntent)
//                .setDeleteIntent(pendingHideIntent)
//                .setSmallIcon(R.mipmap.icon)
//                .setAutoCancel(true)


        startForeground(1, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BackgroundService", "onStartCommand: ")

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}