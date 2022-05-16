package com.ccnio.sec

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat


/**
 * Created by jianfeng.li on 2021/7/16.
 */
private const val TAG = "CourseSurfaceView"

class CourseSurfaceView : View {
    private val screenWidth = 2211.toFloat()
    private val screenHeight = 1080.toFloat()
    private val screenRatio = screenWidth / screenHeight
    private var ratio = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitDrawable by lazy { ContextCompat.getDrawable(context, R.mipmap.ic_launcher) }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawText("hello world", 100f, 10f, paint)
        canvas.drawColor(Color.BLUE)
        canvas.translate(0f, 100f)
        bitDrawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            it.draw(canvas)
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        paint.textSize = 200f

    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }
}