package com.ccino.demo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random
import android.os.Handler
import android.os.Looper
import kotlin.math.max
import kotlin.math.min

class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00FFF7")  // 亮青色
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }

    private val barCount = 30  // 竖条数量
    private var barHeights = FloatArray(barCount) { 0f }
    private val maxHeight = 150f  // 最大高度
    private val minHeight = 5f   // 最小高度
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L  // 更新间隔，1秒

    // 定义几个固定的高度区间
    private val heightLevels = listOf(
        minHeight..minHeight + 30f,  // 低
        minHeight + 30f..maxHeight - 30f,  // 中
        maxHeight - 30f..maxHeight   // 高
    )

    private val updateRunnable = object : Runnable {
        override fun run() {
            barHeights = barHeights.map { 
                // 随机选择一个高度区间
                val level = heightLevels[Random.nextInt(heightLevels.size)]
                // 在选中的区间内生成随机高度
                Random.nextDouble(level.start.toDouble(), level.endInclusive.toDouble()).toFloat()
            }.toFloatArray()
            invalidate()
            handler.postDelayed(this, updateInterval)
        }
    }

    init {
        startAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val barWidth = width.toFloat() / barCount
        val centerY = height / 2f
        
        barHeights.forEachIndexed { index, height ->
            val x = index * barWidth + barWidth / 2
            canvas.drawLine(x, centerY - height, x, centerY + height, paint)
        }
    }

    private fun startAnimation() {
        handler.post(updateRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(updateRunnable)
    }
}
