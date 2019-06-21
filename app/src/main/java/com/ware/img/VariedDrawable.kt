package com.ware.img

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.annotation.IntDef
import com.ware.common.Utils
import kotlin.math.min

/**
 * 圆角、圆形背景，支持阴影
 * 默认 CornerShape
 * 阴影的 radius 会占用view内的空间：radius + fillBg = view(H&W)
 */
class VariedDrawable private constructor(bgColor: Int, private var cornerRadius: Float, var shape: Int, mShadowColor: Int,
                                         private val mShadowRadius: Float, private val mShadowDx: Float, private val mShadowDy: Float) : Drawable() {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBgRect = RectF()

    override fun draw(canvas: Canvas) {
        if (shape == CIRCLE) {
            canvas.drawCircle(mBgRect.centerX(), mBgRect.centerY(), min(mBgRect.width(), mBgRect.height()) / 2, mPaint)
        } else {
            canvas.drawRoundRect(mBgRect, cornerRadius, cornerRadius, mPaint)
        }

        Log.d(TAG, "draw: " + mBgRect.toShortString() + "  " + cornerRadius)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        mBgRect.set(left.toFloat() + mShadowRadius - mShadowDx, top.toFloat() + mShadowRadius - mShadowDy, right.toFloat() - mShadowRadius - mShadowDx, bottom.toFloat() - mShadowRadius - mShadowDy)
        Log.d(TAG, "setBounds 2: " + bounds.toShortString())
    }

    init {
        mPaint.color = bgColor
        // dx、dy 不能大于 shadowRadius
        mPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor)
    }

    companion object {
        const val TAG = "VariedDrawable"
        const val CIRCLE = 1
        const val ROUND = 2
    }

    @IntDef(CIRCLE, ROUND)
    internal annotation class Shape

    internal class Builder(private val view: View) {
        private var mBgColor = Color.RED
        private var mCornerRadius = 0f
        private var mShadowColor = 0
        private var mShape = ROUND

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

        fun setShape(@Shape shape: Int): Builder {
            mShape = shape
            return this
        }

        fun setShadowColor(color: Int): Builder {
            mShadowColor = color
            return this
        }

        private var mShadowRadius = 0f

        fun setShadowRadius(radius: Float): Builder {
            mShadowRadius = Utils.dp2px(radius)
            return this
        }

        private var mShadowDx: Float = 0f

        fun setShadowDx(dx: Float): Builder {
            mShadowDx = Utils.dp2px(dx)
            return this
        }

        private var mShadowDy: Float = 0f

        fun setShadowDy(dy: Float): Builder {
            mShadowDy = Utils.dp2px(dy)
            return this
        }

        fun build(): VariedDrawable {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            val drawable = VariedDrawable(mBgColor, mCornerRadius, mShape, mShadowColor, mShadowRadius, mShadowDx, mShadowDy)
            view.background = drawable
            return drawable
        }
    }


    /**
     * 设置Drawable实例的透明度。
     */
    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        Log.d(TAG, "setAlpha")
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
        Log.d(TAG, "setColorFilter: ")
        mPaint.colorFilter = colorFilter
    }
}