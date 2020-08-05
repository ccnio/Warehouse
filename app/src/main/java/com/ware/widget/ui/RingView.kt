package com.ware.widget.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.face.DisplayUtil

/**
 * Created by jianfeng.li on 20-7-29.
 */
private const val TAG = "RingView"

class RingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val defaultStrokeWidth = DisplayUtil.dip2px(3f)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var color = 0

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RingView)
        color = array.getColor(R.styleable.RingView_color, Color.WHITE)
        val strokeWidth = array.getDimensionPixelOffset(R.styleable.RingView_strokeWidth, defaultStrokeWidth)
        array.recycle()

        Log.d(TAG, "color = $color; strokeWidth = $strokeWidth ")
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center = width / 2f
        val halfStroke = paint.strokeWidth / 2
        canvas.drawCircle(center, center, center - halfStroke, paint)
    }
}