package com.ccnio.ware

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by jianfeng.li on 2017/12/29.
 */
lateinit var app: Application

@HiltAndroidApp
class WareApp : MultiDexApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Log.d("WareApp", "attachBaseContext: ")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("WareApp", "onCreate: ")
        app = this
    }
}