package com.ccino.ware.widget

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
# ViewPager2
由于ViewPager2默认情况下不会预加载出两边的Fragment，相当于默认就是懒加载的，因此如果我们如果没有通过setOffscreenPageLimit()方法设置预加载数量，完全可以不做任何额外处理。
但是对于Fragment很多的情况，由于ViewPager2中的RecyclerView可以缓存Fragment的数量是有限的，因此会造成Fragment的多次销毁和创建，如何解决这个问题呢？
首先设置ViewPager2的预加载数量，让ViewPager2预先创建出所有的Fragment，防止切换造成的频繁销毁和创建。

mViewPager2.setOffscreenPageLimit(mFragments.size());
通过此前示例中Fragment切换时生命周期方法的执行情况我们不难发现不管Fragment是否会被预先创建，只有可见时才会执行到onResume()方法，我们正好可以利用这一规律来实现懒加载，

将Fragment加载数据的逻辑放到onResume()方法中，这样就保证了Fragment可见时才会加载数据。
声明一个变量标记是否是首次执行onResume()方法，因为每次Fragment由不可见变为可见都会执行onResume()方法，需要防止数据的重复加载。
按照以上两点就可以封装我们的懒加载Fragment了，完整代码如下：

public abstract class LazyFragment extends Fragment {
    private boolean isFirstLoad = true; // 是否第一次加载

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            // 将数据加载逻辑放到onResume()方法中
            initData();
            initEvent();
            isFirstLoad = false;
        }
    }
}

 */
class WidgetsActivity : BaseActivity(R.layout.activity_widgets) {
    private val binding by viewBinding(ActivityWidgetsBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewPager2Normal()
        viewPager2Fragment()
        expandableText()
        binding.viewPager.postDelayed({ binding.barView.setData("year") }, 1000)
        numberPicker()
    }

    private fun numberPicker() {
    }

    private fun expandableText() {
        val expandableTextView: ExpandableTextView3 = binding.expandableView
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


        binding.expandableView2.post {
            binding.expandableView2.text = "腹肌撕裂，是一个超高强度的腹肌锻炼视频，因为锻炼完了无论国内外都，有大批的忠实拥趸，更多一是因为动作设计合理因为锻炼完了无论国内外都在全球，" +
                    "随着Flutter被越来越多的知名公司应用在自己中，Flutter这门新技术也逐渐进入了移动开发者的视野"
        }

    }

    private fun viewPager2Fragment() {
        //FragmentStatePagerAdapter被FragmentStateAdapter 替代
        //PagerAdapter被RecyclerView.Adapter替代
        val adapter = PagerFragmentAdapter(this)
        binding.viewPager.adapter = adapter
    }

    private fun viewPager2Normal() {
        val adapter = PagerLayoutAdapter(this)
        binding.viewPager.adapter = adapter
//        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL //竖向
//        viewPager.isUserInputEnabled = false //forbidden 用户滑动
//        viewPager.offscreenPageLimit = 1 //默认值-1时会使用RecyclerView的缓存机制，其它情况与viewpager类似>=1
//        viewPager.setPageTransformer(MarginPageTransformer(50))//replace setPageMargin,页面间距
        val compositePageTransformer = CompositePageTransformer() //可以同时设置多个Transformer
//        compositePageTransformer.addTransformer(ZoomOutPageTransformer())
        val margin = resources.getDimensionPixelOffset(R.dimen.dp_10)
        //一屏多页效果
        compositePageTransformer.addTransformer(MarginPageTransformer(margin))
        binding.viewPager.apply {
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
        binding.viewPager.setPageTransformer(compositePageTransformer)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() { // replace addPageChangeListener
            override fun onPageSelected(position: Int) {
                Log.d("WidgetsActivity", "onPageSelected: $position")
            }
        })

        binding.fakeDragView.setOnClickListener { fakeDragBy() }
    }

    private fun fakeDragBy() {
        binding.viewPager.beginFakeDrag()
        //模拟拖拽。在使用fakeDragBy前需要先beginFakeDrag方法来开启模拟拖拽。。
        val fakeDragBy = binding.viewPager.fakeDragBy(-310f)
        Log.d("WidgetsActivity", "fakeDragBy: $fakeDragBy")
        if (fakeDragBy) {
            binding.viewPager.endFakeDrag()
        }
    }
}
