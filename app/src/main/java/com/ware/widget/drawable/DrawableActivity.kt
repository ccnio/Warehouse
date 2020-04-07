package com.ware.widget.drawable

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.ware.R
import com.ware.img.MDrawableStateView
import com.ware.img.VariedDrawable
import kotlinx.android.synthetic.main.activity_drawable.*


class DrawableActivity : AppCompatActivity(), View.OnClickListener {
    private var flag = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawable)
        tintView.setOnClickListener(this)


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
        } else if (v.id == R.id.tintView) {
            tintDrawable()
        }
        Log.d(TAG, "onClick: ")
    }


    /**
     * change not transparent area color
     */
    private fun tintDrawable() {
        ContextCompat.getDrawable(this, R.drawable.bg_trans)?.let {
            val mutate = it.mutate()
            val wrap = DrawableCompat.wrap(mutate)
            DrawableCompat.setTint(wrap, Color.RED)
            imageView.setImageDrawable(wrap)
        }
    }

    companion object {

        private val TAG = "DrawableActivity"
    }
}
