package com.ware

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.ware.systip.recyclerview.RecyclerDecor
import com.ware.tool.click
import com.ware.tool.setSafeListener
import kotlinx.android.synthetic.main.activity_main_test.*

class MainTestActivity : AppCompatActivity() {

    private var mFlipper: ViewFlipper? = null
    private val di by lazy { RecyclerDecor(12, 12, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)
        mFlipper = findViewById(R.id.flipper)

        mInitView.setOnClickListener {
            di.hashCode()
        }


        val view = View.inflate(this, R.layout.flipper_item, null)
        val tv = view.findViewById<TextView>(R.id.tv)
        tv.text = "abcd"
        val view2 = View.inflate(this, R.layout.flipper_item, null)
        val tv2 = view2.findViewById<TextView>(R.id.tv)
        tv2.text = "我们不一样"
        mFlipper!!.addView(view)
        mFlipper!!.addView(view2)

        view.click {}

        view.setSafeListener {
        }
    }
}
