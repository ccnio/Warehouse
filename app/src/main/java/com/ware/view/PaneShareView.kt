package com.ware.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import com.ware.R
import com.ware.common.Utils
import java.util.*


class PaneShareView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var mCenterY: Float = Utils.getScreenHeight() / 2f
    private var mCenterX: Float = Utils.getScreenWidth() / 2f
    private var mHourPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mMinutePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mHourCircleRadius: Float = resources.getDimension(R.dimen.color_hour_circle_width)
    private var mMinuteCircleRadius: Float = resources.getDimension(R.dimen.color_minute_circle_width)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        val baseRotation = 90f//0点是-90度位置
        /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */
        val mCalendar = Calendar.getInstance()
        val seconds = mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f
        val minuteHandOffset = seconds / 10f//1分钟是6度,1秒分钟走0.1度
        val minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f + minuteHandOffset - baseRotation

        val hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f
        val hour = mCalendar.get(Calendar.HOUR_OF_DAY)
        val hoursRotation = hour * 30 % 360 + hourHandOffset - baseRotation
        /*
             * Save the canvas state before we can begin to rotate it.
             */
        canvas.save()
        val sweepGradient2 = SweepGradient(mCenterX, mCenterY,
                COLORS[hour][2], COLORS[hour][3]
        )
        mMinutePaint.setShader(sweepGradient2)
        canvas.rotate(minutesRotation, mCenterX, mCenterY)
        canvas.drawCircle(mCenterX, mCenterY, mMinuteCircleRadius, mMinutePaint)
        val sweepGradient = SweepGradient(mCenterX, mCenterY,
                COLORS[hour][0], COLORS[hour][1]
        )
        mHourPaint.setShader(sweepGradient)
        canvas.rotate(hoursRotation - minutesRotation, mCenterX, mCenterY)
        canvas.drawCircle(mCenterX, mCenterY, mHourCircleRadius, mHourPaint)
        canvas.restore()
    }

    private val COLORS = arrayOf(
            /*分针开始色  分针最终色  时针开始色  时针结束色*/
            intArrayOf(0x009B2DFF, -0xb6d958, 0x005C00CC, -0x829c1b), //0点
            intArrayOf(0x2600156D, -0xc6db3f, 0x00210DF1, -0xac9301), //1点
            intArrayOf(0x0000156D, -0xec7f01, 0x0000448F, -0xe54501), //2点
            intArrayOf(0x0F2BB2E2, -0xff7436, 0x0F66F2FF, -0xc22f01), //3点
            intArrayOf(0x0F2BB2E2, 0x0F2BB2E2, 0x0F00E9FF, -0xff000f), //4点
            intArrayOf(0x26005A6F, -0xff416c, 0x0000C693, -0xa00040), //5点
            intArrayOf(0x26005A6F, -0xff37ae, 0x0000E83F, -0x6f0064), //6点
            intArrayOf(0x260F7D46, -0xff43f8, 0x26009B7E, -0x7b009c), //7点
            intArrayOf(0x260F7D13, -0xa04400, 0x26009B7E, -0x3200c9), //8点
            intArrayOf(0x0F1E9D5E, -0x623500, 0x0025BC70, -0x1300f0), //9点
            intArrayOf(0x0F1E9D5E, -0x3e2e00, 0x00BCAB25, -0x1000cc), //10点
            intArrayOf(0x00BCAB25, -0x8be, 0x0FFFDF30, -0xdc3), //11点
            intArrayOf(0x0FE5AF1D, -0x1ecf, 0x0FFFDF30, -0x11cf), //12点
            intArrayOf(0x0FE5AF1D, -0x123f00, 0x0FFFDF30, -0x1f00), //13点
            intArrayOf(0x26FF2D00, -0x2800, 0x00FF6C30, -0x25b2), //14点
            intArrayOf(0x26FF2D00, -0x3f00, 0x00FF6C30, -0x3dc8), //15点
            intArrayOf(0x26FF2D00, -0x5c00, 0x00FF6C30, -0x5acc), //16点
            intArrayOf(0x26FF2D00, -0x7e00, 0x00FF6C30, -0x66cf), //17点
            intArrayOf(0x0FBC65AB, -0x42c7c8, 0x0FFF89B5, -0x12868b), //18点
            intArrayOf(0x0FBC65AB, -0x2cad5a, 0x0FFF89B5, -0x128a6a), //19点
            intArrayOf(0x0FBC65AB, -0x439a55, 0x0FFF89B5, -0x764b), //20点
            intArrayOf(0x009B2DFF, -0x619751, 0x005C00CC, -0x338626), //21点
            intArrayOf(0x009B2DFF, -0x80a65b, 0x005C00CC, -0x4d8301), //22点
            intArrayOf(0x009B2DFF, -0xa29b36, 0x005C00CC, -0x606501))//23点

}
