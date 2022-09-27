package com.ccnio.ware.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ccnio.ware.R
import com.google.android.flexbox.*
import com.google.android.material.tabs.TabLayout


/**
 * # TabLayout
 * - 可以使用 tabIndicator 来代替 tabBackground 的动画; tabGravity 设置为 fill 可以根据屏幕宽来自动缩放; tabIndicatorGravity="stretch" 可以使 indicator 与view等高
 * - setTooltipText 去除长按的效果
 * # FlexBoxLayout https://www.jianshu.com/p/d3baa79e3bf8
 * - 可以使用 RecyclerView 代替
 * ## 分割线Divider
 * showDividerHorizontal + dividerDrawableHorizontal ：水平分割线,flexDirection="column"时使用
 * showDividerVertical + dividerDrawableVertical ：垂直分割线，flexDirection="row"时使用
 * showDivider + dividerDrawable ：同时设置水平垂直分割线
 * divider 的 drawable 水平时只需设置 <size android:width="42dp" />，垂直时只需设置  <size android:height="42dp" />
 */
class WidgetsActivity : AppCompatActivity(R.layout.activity_widget) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabLayout()
        flexBoxRecycler1()
        flexBoxRecycler2()
        flexBoxLayout()
    }

    private fun flexBoxLayout() {
        val flexLayout = findViewById<FlexboxLayout>(R.id.flexLayout)
    }

    private fun flexBoxRecycler2() {
        val flexBoxLayout = findViewById<RecyclerView>(R.id.flexRecyclerView)
        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
        flexManager.justifyContent = JustifyContent.FLEX_START //修改对齐方式

        val decoration = FlexboxItemDecoration(this)
        /**
         * divider 的width/height决定 vertical/hor 间隔。但有bug:https://github.com/google/flexbox-layout/issues/449.
         */
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.flexbox_divider))
        flexBoxLayout.addItemDecoration(decoration)
        flexBoxLayout.layoutManager = flexManager
        flexBoxLayout.adapter = FlexBoxAdapter()
    }

    private fun flexBoxRecycler1() {
        val flexBoxLayout = findViewById<RecyclerView>(R.id.flexRecycler2)

        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
        flexManager.justifyContent = JustifyContent.SPACE_BETWEEN //修改对齐方式

        val decoration = FlexboxItemDecoration(this)
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
