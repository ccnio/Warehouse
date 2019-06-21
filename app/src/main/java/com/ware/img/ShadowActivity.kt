package com.ware.img

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.common.Utils
import kotlinx.android.synthetic.main.activity_shadow.*


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
