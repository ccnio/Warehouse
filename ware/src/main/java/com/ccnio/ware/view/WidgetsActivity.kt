package com.ccnio.ware.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import androidx.appcompat.widget.TooltipCompat
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayout


/**
 * # TabLayout
 * - 可以使用 tabIndicator 来代替 tabBackground 的动画; tabGravity 设置为 fill 可以根据屏幕宽来自动缩放; tabIndicatorGravity="stretch" 可以使 indicator 与view等高
 * - setTooltipText 去除长按的效果
 */
class WidgetsActivity : AppCompatActivity(R.layout.activity_widgets) {
    private val binding by viewBinding(com.ccnio.ware.databinding.ActivityWidgetsBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabLayout()
    }

    private fun tabLayout() {
        val tabLout = findViewById<TabLayout>(R.id.tabLayout)
        for (i in 0 until tabLout.tabCount) {
            TooltipCompat.setTooltipText(tabLout.getTabAt(i)!!.view, null)
        }
    }
}
