package com.ware.widget.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.ware.R

/**
 * Created by jianfeng.li on 20-9-1.
 * ratio = w/h
 */
class RatioImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {
    private var ratio = 0f
    private var widthFixed = true

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)
        ratio = typedArray.getFloat(R.styleable.RatioImageView_ratio, ratio)
        widthFixed = typedArray.getInteger(R.styleable.RatioImageView_fixedDirection, 0) == 0
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            var wMeasureSpec = widthMeasureSpec
            var hMeasureSpec = heightMeasureSpec

            if (widthFixed) {
                val width = MeasureSpec.getSize(widthMeasureSpec)
                hMeasureSpec = MeasureSpec.makeMeasureSpec((width / ratio).toInt(), MeasureSpec.EXACTLY)
            } else {
                val height = MeasureSpec.getSize(heightMeasureSpec)
                wMeasureSpec = MeasureSpec.makeMeasureSpec((height * ratio).toInt(), MeasureSpec.EXACTLY)
            }
            super.onMeasure(wMeasureSpec, hMeasureSpec)
        }

    }
}