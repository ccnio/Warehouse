package com.edreamoon.warehouse.img

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
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
import android.graphics.BitmapFactory


class ShadowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadow)

        mCardView.postDelayed({
            Log.d(TAG, "size: Origin = ${Utils.dp2px(100f)}, CardView = ${mCardView.height}, TextView = ${mTextView.height}")
            //Origin = 300.0, CardView = 300, TextView = 300
        }, 2000)


        Log.e("TAG","")

    }

    companion object {
        private const val TAG = "ShadowActivity"
    }
}
