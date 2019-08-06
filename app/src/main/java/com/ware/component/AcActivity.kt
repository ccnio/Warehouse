package com.ware.component

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.common.BaseActivity
import kotlinx.android.synthetic.main.activity_launch_mode.*

/**
启动模式
例子是在一个activity里启动自己，查看生命周期的调用。
- standard: 每次都会调用onCreate(),并且任务栈里有多个此activity的实例
- singleTop:每次调用startActivity()不会再创建activity实例，生命周期也不会回调，会回调onNewIntent()
 */
class AcActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mLaunchModeView -> {
                start(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_mode)
        mLaunchModeView.setOnClickListener(this)
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
            context.startActivity(Intent(context, AcActivity::class.java))
        }
    }
}
