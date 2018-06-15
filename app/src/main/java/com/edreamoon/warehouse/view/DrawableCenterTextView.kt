package com.edreamoon.warehouse.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView

class DrawableCenterTextView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : TextView(context, attributeSet, defStyleAttr) {
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val drawables = compoundDrawables
        val drawableLeft = drawables[0]
        if (drawableLeft != null) {
//            drawableLeft.//
        }
    }
}