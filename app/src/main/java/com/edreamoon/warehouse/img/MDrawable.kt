package com.edreamoon.warehouse.img

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.edreamoon.Utils

class MDrawable private constructor(bgColor: Int, private var cornerRadius: Float) : Drawable() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBgRect = RectF()

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(mBgRect, cornerRadius, cornerRadius, mPaint)
        Log.d(TAG, "draw: " + mBgRect.toShortString() + "  " + cornerRadius)
    }

    fun bindView(view: View) {
        view.background = this
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        mBgRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        Log.d(TAG, "setBounds 2: " + bounds.toShortString())
    }

    class Builder {
        private var mBgColor = Color.RED
        private var mCornerRadius = 0f

        fun setBgColor(color: Int): Builder {
            mBgColor = color
            return this
        }

        /**
         * radius: dp
         */
        fun setCornerRadius(radius: Float): Builder {
            mCornerRadius = Utils.dp2px(radius)
            return this

        }

        fun build(): MDrawable {
            return MDrawable(mBgColor, mCornerRadius)
        }
    }

    init {
        mPaint.color = bgColor
    }

    companion object {
        const val TAG = "MDrawable"
    }


    /**
     * 设置Drawable实例的透明度。
     */
    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    /**
    返回当前Drawable实例的透明或者不透明。返回值是其中之一：
    {@link android.graphics.PixelFormat#UNKNOWN}-透明度未知
    {@link android.graphics.PixelFormat#TRANSLUCENT}-半透明
    {@link android.graphics.PixelFormat#TRANSPARENT}-完全透明
    {@link android.graphics.PixelFormat#OPAQUE}-完全不透明
    如果Drawable中的内容可见性不确定，最安全的方案是返回TRANSLUCENT/半透明
     */
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    //设置滤镜效果
    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }
}