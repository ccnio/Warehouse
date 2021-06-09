package com.ware.widget.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.ware.R
import kotlin.math.min

private const val TAG = "CircleProgressBar"

/**
 * Created by jianfeng.li on 2021/5/27.
 */
class CircleProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val bgColor: Int
    private val progressColor: Int
    private val strokeWidth: Float
    private var progress = 0
    private val progressTotal: Int

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private val circleRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyle, 0)
        bgColor = a.getColor(R.styleable.CircleProgressBar_progressBgColor, Color.YELLOW)
        progressColor = a.getColor(R.styleable.CircleProgressBar_progressColor, Color.RED)
        strokeWidth = a.getDimension(R.styleable.CircleProgressBar_progressStrokeWidth, 0f)
        progressTotal = a.getInt(R.styleable.CircleProgressBar_progressTotal, 100)
        progress = a.getInt(R.styleable.CircleProgressBar_progress, 0)
        a.recycle()

        paint.color = bgColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(w, h) - strokeWidth) / 2f
        centerX = w / 2f
        centerY = h / 2f

        val left = centerX - radius
        val top = centerY - radius
        val right = centerX + radius
        val bottom = centerY + radius
        circleRect.set(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = bgColor
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.color = progressColor
        canvas.drawArc(circleRect, -90f, progress * 360f / progressTotal, false, paint)
    }
}