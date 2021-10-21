package com.ccino.ware.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.ccino.ware.util.dp
import com.ware.R
import kotlin.math.abs
import kotlin.math.max

/**
 * Created by ccino on 2021/9/14.
 */
private const val TAG = "SportRecordBarView"

class RecordBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val dashGap = 50.dp
    private val dashHeight = 1.dp.toFloat()
    private val dashTop = 10.dp
    private val paintDash = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = dashHeight
        style = Paint.Style.STROKE
        color = 0x26000000
        pathEffect = DashPathEffect(floatArrayOf(4.dp.toFloat(), 2.dp.toFloat()), 0f)
    }
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x1a000000
        strokeWidth = 1.dp.toFloat()
    }

    private val textBottom = 2.dp.toFloat()
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x4c000000
        textSize = 10.dp.toFloat()
        textAlign = Paint.Align.LEFT
    }

    private var barWidth = 7.dp
    private var barRadius = barWidth / 2f
    private var barGap = 0f
    private val barHeight = dashTop + dashGap * 4 + dashHeight / 2
    private val barInfoMap = linkedMapOf<Rect, Int>() //todo int -> data
    private val bar = ContextCompat.getDrawable(context, R.drawable.sport_record_bar) as GradientDrawable
    private var dataMax = 0f
    private val texts = mutableListOf<String>()
    private val longPressTime = ViewConfiguration.getLongPressTimeout().toLong()
    private val touchSlop = ViewConfiguration.get(context.applicationContext).scaledTouchSlop

    fun setData(type: String) {
        texts.clear()
        when (type) {
            "mo" -> {
                barWidth = 4.dp
            }
            "year" -> {
                barWidth = 7.dp
                for (i in 1..12) texts.add(i.toString())
            }
            "w" -> {
                barWidth = 13.dp
            }

        }
        barRadius = barWidth / 2f
        bar.cornerRadii = floatArrayOf(barRadius, barRadius, barRadius, barRadius, 0f, 0f, 0f, 0f)

        val maxData = 12 //todo

        val max = max(10, (maxData * 1.2f).toInt())
        val mod = max % 10
        dataMax = (if (mod == 0) max else max + 10 - mod).toFloat()
        Log.d(TAG, "setData: dataMax = $dataMax; slop = $touchSlop; longPress = $longPressTime")

        barGap = (width - texts.size * barWidth) / (texts.size - 1f) + barWidth
        barInfoMap.clear()
        for (i in 0 until texts.size) {
            val left = (i * barGap).toInt()
            val right = left + barWidth
            val bottom = (paddingTop + barHeight).toInt()
            val top = (bottom - i / dataMax * barHeight).toInt() //todo: i->value

            val rect = Rect(left, top, right, bottom)
            barInfoMap[rect] = i
        }
        Log.d(TAG, "setData: rect = ${barInfoMap.keys}")
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.GRAY)

        if (texts.isEmpty()) return
        drawLine(canvas)
        drawText(canvas)
        drawBar(canvas)
    }

    private fun drawBar(canvas: Canvas) {
        barInfoMap.keys.forEach {
            bar.setBounds(it.left, it.top, it.right, it.bottom)
            bar.draw(canvas)
        }
    }

    private fun drawText(canvas: Canvas) {
        val textY = height - textBottom
        paintText.textAlign = Paint.Align.LEFT
        canvas.drawText(texts[0], 0f, textY, paintText)

        paintText.textAlign = Paint.Align.CENTER
        val xOffset = barWidth / 2f
        val size = texts.size
        for (i in 1..size - 2) {
            canvas.drawText(texts[i], xOffset + i * barGap, textY, paintText)
        }

        paintText.textAlign = Paint.Align.RIGHT
        canvas.drawText(texts.last(), width.toFloat(), textY, paintText)

    }

    private fun drawLine(canvas: Canvas) {
        for (i in 0..4) {
            val startY = (paddingTop + dashTop + dashGap * i).toFloat()
            canvas.drawLine(0f, startY, width.toFloat(), startY, paintDash)
        }
        pressedRect?.let {
            val centerX = it.centerX().toFloat()
            canvas.drawLine(centerX, 0f, centerX, barHeight, paintLine)
        }
    }

    private var pressX = 0f
    private var pressY = 0f
    private var pressedRect: Rect? = null
    val selectObserver by lazy { MutableLiveData<Int>() }
    private val runnable = Runnable {
        if (pressedRect == null) return@Runnable
        val data = barInfoMap[pressedRect]
        Log.d(TAG, "run: $data")
        selectObserver.value = data
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressX = x
                pressY = y
                val rect = pressedBarRect() ?: return false
                pressedRect = rect
                postDelayed(runnable, longPressTime)
                Log.d(TAG, "onTouchEvent: down")
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(pressX - x) > touchSlop || abs(pressY - y) > touchSlop) {
                    Log.d(TAG, "onTouchEvent: moved")
                    removeCallbacks(runnable)
                    return false
                }
                Log.d(TAG, "onTouchEvent: move ****************")
            }
            MotionEvent.ACTION_UP -> removeCallbacks(runnable)
        }
        return true
    }

    private fun pressedBarRect(): Rect? {
        val xInt = pressX.toInt()
        val yInt = pressY.toInt()
        Log.d(TAG, "pressedBarRect: x = $xInt; y = $yInt")
        return barInfoMap.keys.find { it.contains(xInt, yInt) }
    }
}