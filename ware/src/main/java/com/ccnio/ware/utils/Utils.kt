package com.ccnio.ware.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import com.ccnio.ware.WareApp
import com.ccnio.ware.app

/**
 * Created by ccino on 2021/12/15.
 */
val <T : Context> T.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun getScreenWidth(): Int {
    val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(outMetrics)
    return outMetrics.widthPixels
}
