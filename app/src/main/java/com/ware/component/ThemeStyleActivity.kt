package com.ware.component

import android.os.Bundle
import com.ware.R
import com.ware.common.BaseActivity

class ThemeStyleActivity : BaseActivity(R.layout.activity_theme_style) {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Day) //must before setContentView
        super.onCreate(savedInstanceState)
    }

}
