package com.ccnio.ware.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.ccnio.ware.R

/**
 * Created by jianfeng.li on 2023/11/24.
 */
class CubicView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private val mPath = Path()
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }

    private val vectorDrawable = resources.getDrawable(R.drawable.wrong, null) as VectorDrawable

    private val mCirClePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }
    private val mFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val width = 126f
    val height = 40f

//    android:pathData="M85.4,3.63C46.47,0.68 1,8.04 1,27C1,60.15 140,57.83 140,27C140,10.97 103.9,10.97 85.4,13.28"

    /**
     * M moveTo, C cubicTo
     */
    val pathPoint = arrayOf(
        arrayOf(27f, 10f),
        arrayOf(34f, 2f, 121f, 3f, 123.5f, 21f),
        arrayOf(125f, 34f, 73.5f, 39.5f, 56f, 39.5f),
        arrayOf(4f, 39.5f, -4f, 21f, 8f, 13f)/**/
    )

    private fun getCubicPath(
        pathPoint: Array<Array<Float>>,
        startX: Float,
        startY: Float,
        scaleX: Float = 1f,
        scaleY: Float = 1f,
        canvas: Canvas
    ) {
        for ((index, point) in pathPoint.withIndex()) {
            if (index == 0) {
                mPath.moveTo(startX + point[0] * scaleX, startY + point[1] * scaleY)
                canvas.drawCircle(point[0], point[1], 4f, pointPaint)
            } else {
                mPath.cubicTo(
                    startX + point[0] * scaleX, startY + point[1] * scaleY,
                    startX + point[2] * scaleX, startY + point[3] * scaleY,
                    startX + point[4] * scaleX, startY + point[5] * scaleY
                )
                canvas.drawCircle(point[4], point[5], 4f, pointPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width, height, mCirClePaint)
        getCubicPath(pathPoint, 0f, 0f, 1f, 1f, canvas)
        canvas.drawPath(mPath, mCirClePaint)

        val h = vectorDrawable.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth
        Log.d("ccino", "onDraw: $h")
        vectorDrawable.setBounds(0, 0, 20, 20)
        vectorDrawable.draw(canvas)

    }


}