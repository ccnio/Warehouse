package com.ware.widget.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ware.face.DisplayUtil
import kotlin.math.abs

/**
 * Created by jianfeng.li on 20-7-28.
 */
private const val TAG = "ReversedTextView"

class ReversedTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    private val txt = "2019.09.20"
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.textSize = DisplayUtil.dip2px(20f).toFloat()
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val txtWidth = paint.measureText(txt)
        setMeasuredDimension(paint.textSize.toInt(), txtWidth.toInt())
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val txtWidth = paint.measureText(txt)
//        setMeasuredDimension(txtWidth.toInt(), paint.textSize.toInt())
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.rotate(270f, width / 2f, height / 2f)
        canvas.drawText(txt, width / 2f, height / 2f + abs(paint.ascent() + paint.descent()) / 2, paint)
        canvas.restore()

    }
}
