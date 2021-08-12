package com.ware.widget

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.databinding.ActivityWidgetsBinding
import com.ware.face.DisplayUtil
import com.ware.jetpack.viewbinding.viewBinding
import com.ware.widget.adpater.PagerFragmentAdapter
import com.ware.widget.transformer.ScaleInTransformer
import com.ware.widget.viewpager2.PagerLayoutAdapter
import com.ware.widget.views.ExpandableTextView3


/**
 * ViewPager2:ViewPager2预加载和缓存
ViewPager2预加载即RecyclerView的预加载，代码在RecyclerView的GapWorker中，这个知识可能有些同学不是很了解，推荐先看这篇博客medium.com/google-deve…；
在ViewPager2上默认开启预加载，表现形式是在拖动控件或者Fling时，可能会预加载一条数据；下面是预加载的示意图：

如何关闭预加载？
((RecyclerView)viewPager.getChildAt(0)).getLayoutManager().setItemPrefetchEnabled(false);
复制代码预加载的开关在LayoutManager上，只需要获取LayoutManager并调用setItemPrefetchEnabled()即可控制开关；
ViewPager2默认会缓存2条ItemView，而且在最新的RecyclerView中可以自定义缓存Item的个数；
public void setItemViewCacheSize(int size) {
mRecycler.setViewCacheSize(size);
}
小结:
预加载和缓存在View层面没有本质的区别，都是已经准备了布局，但是没有加载到parent上；
预加载和离屏加载在View层面有本质的区别，离屏加载的View已经添加到parent上；

链接：https://juejin.im/post/5cda3964f265da035d0c9d8f
 */
class WidgetsActivity : BaseActivity(R.layout.activity_widgets) {
    private val viewBinding by viewBinding(ActivityWidgetsBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewPager2Normal()
        viewPager2Fragment()
        expandableText()
    }

    private fun expandableText() {
        val expandableTextView: ExpandableTextView3 = viewBinding.expandableView
        val viewWidth: Int = DisplayUtil.getScreenWidth()
        expandableTextView.initWidth(viewWidth)
        expandableTextView.maxLines = 3
        expandableTextView.setHasAnimation(true)
        expandableTextView.setCloseInNewLine(true)
        expandableTextView.setOpenSuffixColor(Color.RED)
        expandableTextView.setCloseSuffixColor(resources.getColor(R.color.app_colorPrimary))
        val s = """
    在全球，随着Flutter被越来越多的知名公司应用在自己中，Flutter这门新技术也逐渐进入了移动开发者的视野，尤其是当Google在2018年IO大会上发布了第一个Preview版本后，国内刮起来一股学习Flutter的热潮。
    
    为了更好的方便帮助中国开发者了解这门新技术，我们，Flutter中文网，前后发起了Flutter翻译计划、Flutter开源计划，前是翻译Flutter官方文档，后者则主要是开发一些常用的包来丰富Flutter生态，帮助开发者提高开发效率。而时至今日，这两件事取得的效果还都不错！
    """.trimIndent()
        expandableTextView.setOriginalText(s)


        viewBinding.expandableView2.post { viewBinding.expandableView2.text = "腹肌撕裂，是一个超高强度的腹肌锻炼视频，因为锻炼完了无论国内外都，有大批的忠实拥趸，更多一是因为动作设计合理因为锻炼完了无论国内外都在全球，" +
                "随着Flutter被越来越多的知名公司应用在自己中，Flutter这门新技术也逐渐进入了移动开发者的视野" }

    }

    private fun viewPager2Fragment() {
        //FragmentStatePagerAdapter被FragmentStateAdapter 替代
        //PagerAdapter被RecyclerView.Adapter替代
        val adapter = PagerFragmentAdapter(this)
        viewBinding.viewPager.adapter = adapter
    }

    private fun viewPager2Normal() {
        val adapter = PagerLayoutAdapter(this)
        viewBinding.viewPager.adapter = adapter
//        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL //竖向
//        viewPager.isUserInputEnabled = false //forbidden 用户滑动
//        viewPager.offscreenPageLimit = 1 //默认值-1时会使用RecyclerView的缓存机制，其它情况与viewpager类似>=1
//        viewPager.setPageTransformer(MarginPageTransformer(50))//replace setPageMargin,页面间距
        val compositePageTransformer = CompositePageTransformer() //可以同时设置多个Transformer
//        compositePageTransformer.addTransformer(ZoomOutPageTransformer())
        val margin = resources.getDimensionPixelOffset(R.dimen.dp_10)
        //一屏多页效果
        compositePageTransformer.addTransformer(MarginPageTransformer(margin))
        viewBinding.viewPager.apply {
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
        viewBinding.viewPager.setPageTransformer(compositePageTransformer)

        viewBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() { // replace addPageChangeListener
            override fun onPageSelected(position: Int) {
                Log.d("WidgetsActivity", "onPageSelected: $position")
            }
        })

        viewBinding.fakeDragView.setOnClickListener { fakeDragBy() }
    }

    private fun fakeDragBy() {
        viewBinding.viewPager.beginFakeDrag()
        //模拟拖拽。在使用fakeDragBy前需要先beginFakeDrag方法来开启模拟拖拽。。
        val fakeDragBy = viewBinding.viewPager.fakeDragBy(-310f)
        Log.d("WidgetsActivity", "fakeDragBy: $fakeDragBy")
        if (fakeDragBy) {
            viewBinding.viewPager.endFakeDrag()
        }
    }
}
