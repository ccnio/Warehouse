package com.ware.widget

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.widget.adpater.Pager2Adapter
import com.ware.widget.adpater.PagerFragmentAdapter
import com.ware.widget.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.activity_widgets.*

class WidgetsActivity : BaseActivity(R.layout.activity_widgets) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewPager2Normal()
        viewPager2Fragment()
    }

    private fun viewPager2Fragment() {
        //FragmentStatePagerAdapter被FragmentStateAdapter 替代
        //PagerAdapter被RecyclerView.Adapter替代
        val adapter = PagerFragmentAdapter(this)
        viewPager.adapter = adapter
    }

    private fun viewPager2Normal() {
        val adapter = Pager2Adapter(this)
        viewPager.adapter = adapter
//        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL //竖向
//        viewPager.isUserInputEnabled = false //forbidden 用户滑动
//        viewPager.offscreenPageLimit = 1 //默认值-1时会使用RecyclerView的缓存机制，其它情况与viewpager类似>=1
//        viewPager.setPageTransformer(MarginPageTransformer(50))//replace setPageMargin,页面间距
        val compositePageTransformer = CompositePageTransformer() //可以同时设置多个Transformer
//        compositePageTransformer.addTransformer(ZoomOutPageTransformer())
        val margin = resources.getDimensionPixelOffset(R.dimen.dp_10)
        //一屏多页效果
        compositePageTransformer.addTransformer(MarginPageTransformer(margin))
        viewPager.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                val padding = margin + margin
                // setting padding on inner RecyclerView puts overscroll effect in the right place
                setPadding(padding, 0, padding, 0)
                clipToPadding = false
            }
        }
        compositePageTransformer.addTransformer(ScaleInTransformer())
        viewPager.setPageTransformer(compositePageTransformer)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() { // replace addPageChangeListener
            override fun onPageSelected(position: Int) {
                Log.d("WidgetsActivity", "onPageSelected: $position")
            }
        })

        fakeDragView.setOnClickListener { fakeDragBy() }
    }

    private fun fakeDragBy() {
        viewPager.beginFakeDrag()
        //模拟拖拽。在使用fakeDragBy前需要先beginFakeDrag方法来开启模拟拖拽。。
        val fakeDragBy = viewPager.fakeDragBy(-310f)
        Log.d("WidgetsActivity", "fakeDragBy: $fakeDragBy")
        if (fakeDragBy) {
            viewPager.endFakeDrag()
        }
    }
}
