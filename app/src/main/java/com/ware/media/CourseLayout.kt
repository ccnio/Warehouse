package com.ware.media

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.ware.face.DisplayUtil

/**
 * Created by jianfeng.li on 2021/7/26.
 */
private const val TAG = "CourseLayout"

/**
 * # ViewGroup onDraw不调用： setWillNotDraw(false)
 * # layout影响children的最终sie
 * # 绘制从窗口左上角，想超出屏幕只能通过api如,layout,canvas操作来进行
 * # onMeasure 一定要调用 measureChildren(widthMeasureSpec, heightMeasureSpec)，否则children无法确定大小
 * # onLayout,onMeasure不处理children,children不会显示
 * # surface 无大小时不进行绘制
 *
 */
class CourseLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {
    private val screenHeight = DisplayUtil.getScreenHeightPx(context)
    private val screenWidth = DisplayUtil.getScreenWidthPx(context)
    private val screenRatio = screenWidth / screenHeight
    private var ratio = 0f
    private var videoWidth = 0
    private var videoHeight = 0
    private var finalVideoW = screenWidth
    private var finalVideoH = screenHeight

    init {
        setWillNotDraw(false)
    }

    fun setRation(videoWidth: Int, videoHeight: Int) {
        Log.d(TAG, "setRation: sw = $screenWidth; sh = $screenHeight; statusBar = ${DisplayUtil.getStatusBarHeight()}")
        if (videoHeight == 0 || videoWidth == 0) return
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        ratio = videoWidth.toFloat() / videoHeight
        if (ratio <= screenRatio) {
            finalVideoH = (screenWidth * 1f / videoWidth * videoHeight).toInt()
            finalVideoW = screenWidth
        } else {
            finalVideoW = (screenHeight * 1f / videoHeight * videoWidth).toInt()
            finalVideoH = screenHeight
        }
        Log.d(TAG, "setRation: videoW = $videoWidth; videoH = $videoHeight; finalW = $finalVideoW; finalH = $finalVideoH")
//        invalidate()
        requestLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (finalVideoW == screenWidth) {
            val offset = (finalVideoH - screenHeight) / 2
            getChildAt(0).layout(0, -offset, screenWidth, finalVideoH - offset)
//            getChildAt(0).layout(0, 0, 200, 500)
        } else if (finalVideoH == screenHeight) {
            val offset = (finalVideoW - screenWidth) / 2
            getChildAt(0).layout(-offset, 0, finalVideoW - offset, screenHeight)
        }
        getChildAt(1).layout(0, 0, screenWidth, screenHeight)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(finalVideoW, finalVideoH)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
    }
}