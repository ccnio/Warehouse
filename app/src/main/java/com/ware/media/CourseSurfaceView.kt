package com.ware.media

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView

/**
 * Created by jianfeng.li on 2021/7/16.
 */
private const val TAG = "CourseSurfaceView"

class CourseSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : SurfaceView(context, attrs, defStyle) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged: $w,$h")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw: w = $width; h = $height")
    }
}