package com.ware.widget.viewpager2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ware.R
import com.ware.widget.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.activity_view_pager2.*

private const val TAG = "ViewPager2Activity"

/**
 * 0. 只有两种adapter
 *    FragmentStatePagerAdapter被FragmentStateAdapter 替代
 *    PagerAdapter被RecyclerView.Adapter替代
 */

class ViewPager2Activity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(PagerShareVM::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2)
        pageFragment()
        pageLayout()
    }

    /**
     * 1. fragment 生命周期。
     *    不设置offscreenPageLimit的话，首次进入(5)只加载当前页，不进行预加载。  5 new fragment->5 onCreate->5 onResume. fragmentPager.setCurrentItem(int item, boolean smoothScroll),smoothScroll为true时，首次进入会加载多条
     *    滑动到下一页(6)时，6 new fragment->6 onCreate->7 new fragment->5 onPause->6 onResume. 注意点: 1.提前实例化7(只实例化); 2.离屏界面(5)执行到onPause;
     *    滑动到达一定数量后，最初创建的fragment被销毁（onDestroyView,onDestroy），缓存里会有最近创建的个别界面
     *
     *    根据以上及live data在onPause时依旧为active state，所以离屏存活的界面注册live data的话依旧可以收到更新.eg 5滑动到7时,在7发送更新数据,打印如下
     *    ViewPager2Activity PagerFragment onCreate: this is data 7
     *    page 5: received changed data = this is data 7
     *    page 6: received changed data = this is data 7
     *    page 7: received changed data = this is data 7
     *    此时8只处于实例创建阶段，inactive state,所以收不到数据，当滑动到8时才会收到：page 8: received changed data = this is data 7
     *
     * 2. 获取当前fragment: fragmentAdapter.getCurrentPager(fragmentPager.currentItem) 暂时找不到更好办法
     *
     * 3. 当前fragment数据加载完毕后传给所在host
     *    fragments的数据加载返回顺序无法确定，并且缓存的离屏Fragment 生命处理pause造成live data依旧是active的，所以每个fragment获取到数据后需要依据是否isResumed再决定是否通过live data发送
     *
     * 4. offscreenPageLimit = 1
     *    首次进入 5 new fragment 5, onCreate 5 onResume -> 6 new fragment, 6 onCreate -> 4 new fragment, 4 onCreate （预加载的6,4只到onCreate）
     *    滑到下页(6) 7 new fragment, 7 onCreate -> 8 new fragment -> 5 onPause -> 6 onResume (会实例8,顺序也和想的不一样)
     *
     * 5. ViewPager2中的懒加载方案
     *    由于ViewPager2默认情况下不会预加载出两边的Fragment，相当于默认就是懒加载的，因此如果我们如果没有通过setOffscreenPageLimit()方法设置预加载数量，完全可以不做任何额外处理。
     *    但是对于Fragment很多的情况，由于ViewPager2中的RecyclerView可以缓存Fragment的数量是有限的，因此会造成Fragment的多次销毁和创建，如何解决这个问题呢？
     *    a.首先设置ViewPager2的预加载数量，让ViewPager2预先创建出所有的Fragment，防止切换造成的频繁销毁和创建。mViewPager2.setOffscreenPageLimit(mFragments.size());
     *    b.前面可知不管Fragment是否会被预先创建，只有可见时才会执行到onResume()方法，我们正好可以利用这一规律来实现懒加载，声明一个变量标记是否是首次执行onResume()方法，
     *    将Fragment加载数据的逻辑放到onResume()方法中，这样就保证了Fragment可见时才会加载数据。
     */
    private fun pageFragment() {
        val fragmentAdapter = Pager2FragmentAdapter(this)
        fragmentPager.adapter = fragmentAdapter
        fragmentPager.offscreenPageLimit = 1
        fragmentPager.setCurrentItem(fragmentAdapter.itemCount / 2, false)
        fragmentAdapter.getCurrentPager(fragmentPager.currentItem)
        fragmentPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected: $position")
            }
        })

        viewModel.data.observe(this, Observer { Log.d(TAG, "ViewPager2Activity PagerFragment onCreate: $it") })
    }


    /**
     * 1.View周期默认情况
     *   首次进入5 onCreateViewHolder: ->onBindViewHolder: 5->onViewAttachedToWindow: 5
     *   滑动到6时 onCreateViewHolder:->onBindViewHolder: 6->onViewAttachedToWindow: 6->onCreateViewHolder:->onBindViewHolder: 7->onViewDetachedFromWindow: 5(注意：7预加载了)
     *   滑动到8时 5会被回收 onViewRecycled: 5; 继续到9时,由于已经有被回收的view，所以不再执行onCreateViewHolder
     */
    private fun pageLayout() {
        val layoutAdapter = PagerLayoutAdapter(this)
        layoutPager.adapter = layoutAdapter
        layoutPager.setCurrentItem(0,/*layoutAdapter.itemCount / 2*/false)

        val compositePageTransformer = CompositePageTransformer() //可以同时设置多个Transformer
//        compositePageTransformer.addTransformer(ZoomOutPageTransformer())

        //一屏多页效果
        val margin = resources.getDimensionPixelOffset(R.dimen.dp_10)
        compositePageTransformer.addTransformer(MarginPageTransformer(margin))
        layoutPager.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                val padding = margin + margin
                // setting padding on inner RecyclerView puts overscroll effect in the right place
                setPadding(padding, 0, padding, 0)
                clipToPadding = false//clipToPadding为false时，表示布局能够被绘制到padding区域
            }
        }

        //滑动缩放效果
        compositePageTransformer.addTransformer(ScaleInTransformer())
        layoutPager.setPageTransformer(compositePageTransformer)

        layoutPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("OnPageChangeCallback", "onPageSelected: $position")
            }

            /**
            5->6
            D: onPageScrolled: pos = 5; posOffset = 0.053703703; posPix = 58
            D: onPageScrolled: pos = 5; posOffset = 0.11481482; posPix = 124
            D: onPageScrolled: pos = 5; posOffset = 0.24444444; posPix = 264
            D: onPageSelected: 6 (注意此句)
            D: onPageScrolled: pos = 5; posOffset = 0.32777777; posPix = 354
            ......
            D: onPageScrolled: pos = 5; posOffset = 0.99814814; posPix = 1078
            D: onPageScrolled: pos = 6; posOffset = 0.0; posPix = 0

            6->5
            D: onPageScrolled: pos = 5; posOffset = 0.94166666; posPix = 1017
            D: onPageScrolled: pos = 5; posOffset = 0.7074074; posPix = 764
            D: onPageSelected: 5(注意此句)
            D: onPageScrolled: pos = 5; posOffset = 0.6333333; posPix = 684
            D: onPageScrolled: pos = 5; posOffset = 0.1037037; posPix = 112
            D: onPageScrolled: pos = 5; posOffset = 0.075; posPix = 81
            ......
            D: onPageScrolled: pos = 5; posOffset = 0.0; posPix = 0
             */
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d("OnPageChangeCallback", "onPageScrolled: pos = $position; posOffset = $positionOffset; posPix = $positionOffsetPixels")
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("OnPageChangeCallback", "onPageScrollStateChanged: state = $state")
            }
        })
    }
}