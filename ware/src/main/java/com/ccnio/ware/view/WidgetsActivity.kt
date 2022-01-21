package com.ccnio.ware.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ccnio.ware.R
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayout


/**
 * # TabLayout
 * - 可以使用 tabIndicator 来代替 tabBackground 的动画; tabGravity 设置为 fill 可以根据屏幕宽来自动缩放; tabIndicatorGravity="stretch" 可以使 indicator 与view等高
 * - setTooltipText 去除长按的效果
 * # FlexBoxLayout
 * - 可以使用 RecyclerView 代替
 */
class WidgetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)
        tabLayout()
        flexBoxLayout()
        flexBoxTab()
    }

    private fun flexBoxTab() {
        val flexBoxLayout = findViewById<RecyclerView>(R.id.flexRecyclerView)
        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
        flexManager.justifyContent = JustifyContent.FLEX_START //修改对齐方式

        val decoration = FlexboxItemDecoration(this)
        /**
         * divider 的width/height决定 vertical/hor 间隔。但有bug:https://github.com/google/flexbox-layout/issues/449. update: 设置item固定高度好像可以解决
         */
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.flexbox_divider))
        flexBoxLayout.addItemDecoration(decoration)
        flexBoxLayout.layoutManager = flexManager
        flexBoxLayout.adapter = FlexBoxAdapter()
    }

    private fun flexBoxLayout() {
        val flexBoxLayout = findViewById<RecyclerView>(R.id.flexTabLayout)

        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
        flexManager.justifyContent = JustifyContent.SPACE_BETWEEN //修改对齐方式

        val decoration = FlexboxItemDecoration(this)
        /**
         * divider 的width/height决定 vertical/hor 间隔。但有bug:https://github.com/google/flexbox-layout/issues/449.
         */
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.flexbox_divider))
        flexBoxLayout.addItemDecoration(decoration)
        flexBoxLayout.layoutManager = flexManager
        flexBoxLayout.adapter = FlexBoxTabAdapter()
    }

    private fun tabLayout() {
        val tabLout = findViewById<TabLayout>(R.id.tabLayout)
        for (i in 0 until tabLout.tabCount) {
            TooltipCompat.setTooltipText(tabLout.getTabAt(i)!!.view, null)
        }
    }
}
