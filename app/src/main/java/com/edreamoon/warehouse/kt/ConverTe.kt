package com.edreamoon.warehouse.kt

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

import com.edreamoon.warehouse.img.MDrawable

class ConverTe private constructor() : Drawable() {

    private val mPaint: Paint

    class Builder {
        private var color: Int = 0

        init {
            color = 2
        }

        fun setColor(c: Int) {
            color = c
        }
    }

    init {
        mPaint = Paint()
        mPaint.color = Color.RED
    }

    override fun draw(canvas: Canvas) {

    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}
