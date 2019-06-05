package com.ware.img

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.ware.R
import kotlinx.android.synthetic.main.activity_drawable.*


class DrawableActivity : AppCompatActivity(), View.OnClickListener {
    private var flag = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawable)


        /**
         * 圆角、圆形、阴影
         */
        VariedDrawable.Builder(findViewById(R.id.circle))
                .setBgColor(Color.GREEN)
                .setShape(VariedDrawable.CIRCLE)
                .setShadowColor(Color.BLACK)
                .setShadowRadius(5f)
//                .setShadowDx(2f) //dx、dy 不能大于 shadowRadius
//                .setShadowDy(2f)
                .build()


        /**
         * 自定义view 状态
         */
        stateDrawable.setOnClickListener(this)

//        drawShadow(this, mShadowView)
        /**
         * 圆角、圆形、阴影
        //         */
//        VariedDrawable.Builder(findViewById(R.id.mShadowView))
//                .setBgColor(Color.GREEN)
//                .setShape(VariedDrawable.Builder.ROUND)
//                .setShadowColor(Color.BLACK)
//                .setShadowRadius(5f)
////                .setShadowDx(2f) //dx、dy 不能大于 shadowRadius
////                .setShadowDy(2f)
//                .build()

        VariedDrawable.Builder(mCornerView)
                .setBgColor(Color.BLUE)
                .setCornerRadius(5f).build()


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
