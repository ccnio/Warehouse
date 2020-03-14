package com.ware.component.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.common.BaseActivity
import com.ware.kt.KtActivity
import com.ware.systip.SecActivity
import kotlinx.android.synthetic.main.activity_launch_mode.*

class MFragmentActivity : BaseActivity(R.layout.activity_fragmnet), View.OnClickListener {



    override fun onClick(v: View) {
        when (v.id) {
            R.id.mLaunchModeView -> {
                start(this)
            }
            R.id.mBgAcView -> {
                startActivityBackground()
            }
            R.id.mWakeLockView -> {
                mWakeLockView.postDelayed({ com.ware.systip.start() }, 1000 * 10)
            }
            R.id.mInfoView -> {
                SecActivity.start(this)
            }
        }
    }

    private fun startActivityBackground() {
        mBgAcView.postDelayed({
            Log.d("AcActivity", "startActivityBackground: ${System.currentTimeMillis()}")
            startActivity(Intent(this@MFragmentActivity, KtActivity::class.java))
        }, 3000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_mode)
        mLaunchModeView.setOnClickListener(this)
        mBgAcView.setOnClickListener(this)
        mWakeLockView.setOnClickListener(this)
        mInfoView.setOnClickListener(this)
        Log.d("AcActivity", "onCreate: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d("AcActivity", "onStart: ")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("AcActivity", "onNewIntent: ${intent?.action}")
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MFragmentActivity::class.java))
        }
    }
}
