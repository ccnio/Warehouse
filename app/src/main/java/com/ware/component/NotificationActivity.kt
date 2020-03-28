package com.ware.component

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ware.R
import kotlinx.android.synthetic.main.activity_notification.*


/**
 * 渠道将通知以组划分，从而不同通知可以设置不同重要性等特性
 */
const val channel_1 = "channel_1"
const val channel_2 = "channel_2"

private const val NOTIFY_ID_1 = 0x01
private const val NOTIFY_ID_2 = 0x02

class NotificationActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mCommonView -> {
                commonNotify()
            }
            R.id.mFullScreenView -> {
                fullScreenNotify()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        mCommonView.setOnClickListener(this)
        mFullScreenView.setOnClickListener(this)
    }

    private fun commonNotify() {
        Log.d("NotificationActivity", "commonNotify: ")
        val intent = Intent(this, AcActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channel_1, "Channel_1 name", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)//是否在桌面icon右上角展示小红点
            channel.lightColor = Color.GREEN //小红点颜色
            channel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
            channel.description = "channel_1 description"
            notificationManager.createNotificationChannel(channel)
        }

        //builder notify
        val notificationBuilder = NotificationCompat.Builder(this, channel_1)
                .setSmallIcon(R.drawable.bit)
                .setContentTitle("common notification")
                .setContentText("content text~")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(pendingIntent)
        NotificationManagerCompat.from(this).notify(NOTIFY_ID_1, notificationBuilder.build())
    }

    private fun fullScreenNotify() {
        Log.d("NotificationActivity", "fullScreenNotify: ")
        val fullScreenIntent = Intent(this, AcActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channel = NotificationChannel(channel_2, "Channel_2 name", NotificationManager.IMPORTANCE_HIGH)//必须high,修改设置后必须重置app数据不然不生效
        channel.enableLights(true)//是否在桌面icon右上角展示小红点
        channel.lightColor = Color.GREEN //小红点颜色
        channel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
        channel.description = "channel_2 description"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, channel_2)
                .setSmallIcon(R.drawable.bit)
                .setContentTitle("Incoming call")
                .setContentText("(919) 555-1234")
                .setCategory(NotificationCompat.CATEGORY_CALL)

                // Use a full-screen intent only for the highest-priority alerts where you
                // have an associated activity that you would like to launch after the user
                // interacts with the notification. Also, if your app targets Android Q, you
                // need to request the USE_FULL_SCREEN_INTENT permission in order for the
                // platform to invoke this notification.
                .setFullScreenIntent(fullScreenPendingIntent, true)

        NotificationManagerCompat.from(this).notify(NOTIFY_ID_2, notificationBuilder.build())
    }
}
