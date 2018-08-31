package com.edreamoon.warehouse.compatibility

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.activity_status_bar.*

class StatusBarActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mFullScreen -> window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            R.id.mTransparent -> window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            R.id.mCutInfo -> window.

//                else ->
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_bar)
        mFullScreen.setOnClickListener(this)
        mCutInfo.setOnClickListener(this)
    }
}
