package com.ware.widget.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.ware.widget.R


/**
 * Created by jianfeng.li on 19-6-24.
 */
class AttrView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : View(context, attr, defStyle) {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBgDrawable: Drawable? = null

    init {
        attr?.let {

            val typedArray = context.obtainStyledAttributes(attr, R.styleable.AttrView)

            /**
             * color format: app:bg="@color/widget_gray" or app:bg="#66000000"
             * get:
             * val color = typedArray.getColor(R.styleable.AttrView_bg, Color.WHITE)
             */
//            val color = typedArray.getColor(R.styleable.AttrView_bg, Color.WHITE)
//            mPaint.color = color

            /**
             * reference-drawable format:  app:bg="@drawable/icon_album"
             * get:
             * val mBgDrawable:Drawable? = typedArray.getDrawable(R.styleable.AttrView_bg)  or
             * val resourceId = typedArray.getResourceId(R.styleable.AttrView_bg, R.drawable.widget_attr)
             * mBgDrawable = ContextCompat.getDrawable(context, resourceId)
             */

//            mBgDrawable = typedArray.getDrawable(R.styleable.AttrView_bg)

            /**
             * multiple type format: <attr name="bg" format="reference|color" />
             * get:
             */
//            val type = typedArray.getType(R.styleable.AttrView_bg) request  api 21; or get as follows
            val bgValue = TypedValue()
            typedArray.getValue(R.styleable.AttrView_bg, bgValue)
            if (bgValue.type == TypedValue.TYPE_FIRST_COLOR_INT) {
                Log.d("AttrView", "color type")
                mPaint.color = typedArray.getColor(R.styleable.AttrView_bg, Color.RED)
            } else {
                Log.d("AttrView", "drawable type ${bgValue.type}") // type is TYPE_STRING not TYPE_REFERENCE; this is cannot understand
                mBgDrawable = typedArray.getDrawable(R.styleable.AttrView_bg)
            }

            /**
             * print all attribute include sys
             */
            val count = attr.attributeCount
            for (i in 0 until count) {
                val attrName = attr.getAttributeName(i)
                val attrVal = attr.getAttributeValue(i)
                Log.e("AttrView", "attrName = $attrName , attrVal = $attrVal")
            }
            Log.d("AttrView", "*(***************************: ")
            typedArray.recycle()
        }

    }

    override fun onDraw(canvas: Canvas) {
        if (mBgDrawable != null) {
            mBgDrawable!!.setBounds(0, 0, width, height)
            mBgDrawable!!.draw(canvas)
        } else {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
        }
        super.onDraw(canvas)
    }
}