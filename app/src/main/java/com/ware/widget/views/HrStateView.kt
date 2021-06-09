package com.ware.widget.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.ware.R
import com.ware.face.DisplayUtil

private const val TAG = "CircleProgressBar"

/**
 * Created by jianfeng.li on 2021/5/27.
 */
class HrStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val startColor = -0xbd396e
    private val midColor = -0x331b8
    private val endColor = -0xf98b5
    private var lineY = 0f
    private var lineHeight = 0f
    private var lineStartX = 0f
    private var lineEndX = 0f
    private val evaluator = ArgbEvaluatorCompat.getInstance()
    private var minHr = 0
    private var maxHr = 50
    private var curHr = 25
    private val desc = "有氧"
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val testPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val popRadius = DisplayUtil.dip2px(23f).toFloat()
    private val popPadding = DisplayUtil.dip2px(7f)
    private val halfPopHeight: Float = resources.getDimension(R.dimen.course_hr_pop_height) / 2
    private val descYOffset = DisplayUtil.dip2px(4f)

    private val popRectF = RectF()

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        testPaint.color = Color.WHITE
        testPaint.textAlign = Paint.Align.CENTER
        testPaint.textSize = DisplayUtil.dip2px(12f).toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val shader = LinearGradient(0f, 0f, w.toFloat(), 0f, intArrayOf(startColor, midColor, endColor), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        paint.shader = shader
        lineHeight = h * 0.2f
        paint.strokeWidth = lineHeight
        lineY = h / 2f
        val half = lineHeight / 2
        lineStartX = half
        lineEndX = w - half

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLUE)
        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, paint)

        val midHr = (minHr + maxHr).shr(1)
        if (curHr < midHr) return

        val fraction = (curHr - midHr + 1).toFloat() / (maxHr - midHr + 1)
        val evaluate = evaluator.evaluate(fraction, midColor, endColor)
        testPaint.color = evaluate
        canvas.drawCircle(width / 2f, 200f, 50f, testPaint)


        val descCx = (0.5f + 0.5f * fraction) * width
        val descWidth = testPaint.measureText(desc)
        val halfPopWidth = descWidth / 2 + popPadding
        popRectF.set(descCx - halfPopWidth, lineY - halfPopHeight, descCx + halfPopWidth, lineY + halfPopHeight)
        canvas.drawRoundRect(popRectF, popRadius, popRadius, testPaint)

        testPaint.color = Color.WHITE
        canvas.drawText(desc, descCx, lineY + descYOffset, testPaint)
    }

    fun setHr(hr: Int) {
        curHr = hr
        invalidate()
    }

    fun setHrRange(start: Int, end: Int) {
        minHr = start
        maxHr = end
    }
}