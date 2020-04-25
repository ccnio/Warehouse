package com.ware.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.ware.R


/**
 * Created by jianfeng.li on 19-6-24.
 */
class AttrView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0)
    : View(context, attr, defStyleAttr, defStyleRes) {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var drawable: Drawable? = null

    init {
        /**
         * 1. get attr by styleable
         * app:txtColor="@color/app_colorPrimary"
         * app:bgDrawable="@drawable/green_girl"
         */
        //val typedArray = context.obtainStyledAttributes(attr, R.styleable.AttrView, defStyleAttr, defStyleRes)
        //val color = typedArray.getColor(R.styleable.AttrView_txtColor, Color.RED)
        //drawable = typedArray.getDrawable(R.styleable.AttrView_bgDrawable)!!

        /**
         * 2. get attr by style in xml (only get first one,why?)
         * style="@style/AttrStyle"
         */
//        val attrArray = intArrayOf(R.attr.bgDrawable)
//        val typedArray = context.obtainStyledAttributes(attr, attrArray, defStyleAttr, defStyleRes)
//        val color = typedArray.getColor(0, Color.RED)
//        //drawable = typedArray.getDrawable(0)

        /**
         * 3. default attr by defStyleAttr(应用于View的默认样式（定义于Theme中）)
         *  找不到的attr从默认defStyleAttr里找
         */
//        val typedArray = context.obtainStyledAttributes(attr, R.styleable.AttrView, R.attr.myDefaultAttr, defStyleRes)
//        val color = typedArray.getColor(R.styleable.AttrView_txtColor, Color.RED)
//        drawable = typedArray.getDrawable(R.styleable.AttrView_bgDrawable)!!

        /**
         * 4. default attr by defStyleRes(最后的选择)
         */
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.AttrView, 0, R.style.DefaultStyle)
        val color = typedArray.getColor(R.styleable.AttrView_txtColor, Color.RED)
        drawable = typedArray.getDrawable(R.styleable.AttrView_bgDrawable)!!

        typedArray.recycle()
        paint.color = color
        paint.textSize = 200f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawable?.setBounds(0, 0, width, height)
        drawable?.draw(canvas)
        canvas.drawText("AbcD", 50.toFloat(), 150.toFloat(), paint)
    }

}