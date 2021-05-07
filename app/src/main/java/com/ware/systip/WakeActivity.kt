package com.ware.systip

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardDismissCallback
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.WareApp

fun start() {
    val context = WareApp.sContext
    val intent = Intent(context, WakeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    intent.setPackage(context.packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            or Intent.FLAG_FROM_BACKGROUND
            or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            or Intent.FLAG_ACTIVITY_NO_ANIMATION
            or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    pendingIntent.send()

//    context.startActivity(intent)
}

private const val TAG = "WakeActivity"

class WakeActivity : AppCompatActivity() {

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        keyguardManager.requestDismissKeyguard(this, object : KeyguardDismissCallback() {
            override fun onDismissError() {
                super.onDismissError()
                Log.d(TAG, "onDismissError: ")
            }

            override fun onDismissSucceeded() {
                super.onDismissSucceeded()
                Log.d(TAG, "onDismissSucceeded: ")
            }

            override fun onDismissCancelled() {
                super.onDismissCancelled()
                Log.d(TAG, "onDismissCancelled: ")
            }
        })


        setContentView(R.layout.activity_wake)
    }

    override fun onResume() {
        super.onResume()
        Log.d("WakeActivity", "onResume: ")
    }
}
