package com.edreamoon.warehouse.img

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.edreamoon.Utils
import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.activity_shadow.*

class ShadowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadow)

        mCardView.postDelayed({
            Log.d(TAG, "size: Origin = ${Utils.dp2px(100f)}, CardView = ${mCardView.height}, TextView = ${mTextView.height}")
            //Origin = 300.0, CardView = 300, TextView = 300
        }, 2000)

//        var strList = arrayListOf("顶戴茜模压要东奔西走", "其实啦协调口是")
//        for (str in strList) {
//            val textView = TextView(this)
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
//            textView.setTextColor(Color.WHITE)
//            textView.setEms(1)
//            textView.minWidth = Utils.dp2px(18f).toInt()
//            textView.gravity = Gravity.RIGHT
//            textView.text = str
//            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            params.leftMargin = Utils.dp2px(8f).toInt()
//            textView.layoutParams = params
//            mMottoLayout.addView(textView)
//        }
        val drawable = getDrawable(R.drawable.share_aqi_warn) as GradientDrawable
        drawable.setColor(Color.GREEN)

        mGrad.background = drawable

    }

    companion object {
        private const val TAG = "ShadowActivity"
    }
}
