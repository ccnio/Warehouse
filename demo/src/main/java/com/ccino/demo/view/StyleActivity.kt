package com.ccino.demo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.ccino.demo.R

private const val TAG = "StyleActivity"

/**
 * 为每个 view 添加额外的 attrs
 */
class StyleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_style)
    }
}
