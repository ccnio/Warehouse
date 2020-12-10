package com.ware.performance

import android.os.Bundle
import android.view.View
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_memory.*


/**
 */
private const val TAG = "FpsActivity"

class FpsActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fps)
        leakView.setOnClickListener(this)
        bitView.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}
