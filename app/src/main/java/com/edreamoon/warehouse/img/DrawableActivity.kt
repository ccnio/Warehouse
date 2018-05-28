package com.edreamoon.warehouse.img

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.activity_drawable.*
import java.io.Closeable


class DrawableActivity : AppCompatActivity(), View.OnClickListener {
    private var flag = true
//    private var mStateView: MDrawableStateView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawable)


        /**
         * 圆角、圆形、阴影
         */
        ShapeDrawable.Builder(findViewById(R.id.circle))
                .setBgColor(-0xd10de)
                .setShape(ShapeDrawable.Builder.CIRCLE)
                .setShadowColor(Color.DKGRAY)
                .setShadowRadius(5f)
                .setShadowDx(2f) //dx、dy 不能大于 shadowRadius
                .setShadowDy(2f)
                .build()


        /**
         * 自定义view 状态
         */
        stateDrawable.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.stateDrawable) {
            stateDrawable!!.setState(if (flag) MDrawableStateView.TWO else MDrawableStateView.ONE)
            flag = !flag
        }
        Log.d(TAG, "onClick: ")
    }

    companion object {

        private val TAG = "DrawableActivity"
    }
}
