package com.ccino.demo.media.list

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityDyBinding
import com.ccino.demo.media.videoList
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSpec

private const val TAG = "DyActivity"

/**
 * # viewPager2 首次加载指定页面时，注意事项
 * - setCurrentItem(position, false) 在设置 adapter 之后调用，否则不会生效。
 * - setCurrentItem(position, true)即 smoothScroll=true 时，会触发 onPageScrollStateChanged，且会异常加载好几个界面
 *   所以使用 setCurrentItem(position, false)：但需要明确指定 offscreenPageLimit(默认1，预加载相邻两个页面)，否则初始化时不会预加载。
 *   首次初始化 callback 在 setCurrentItem 前则会触发 onPageSelected (无 onPageScrollStateChanged)，否则不会触发 onPageSelected。
 * - setCurrentItem(defaultPos, false)：viewpager2 + fragment 且不指定 offscreenPageLimit时，不会预加载相邻页面
 *
 * # 往下一页 6 滑动时，调用顺序：
 *  onBindViewHolder: 7 （预加载下一页）
 *  onViewAttachedToWindow: DyData(url=https://v-cdn.zjol.com.cn/276970.mp4, title=视频7)
 *  onPageSelected: 6 （要加载的页固定）
 *  onViewDetachedFromWindow: DyData(url=https://v-cdn.zjol.com.cn/276972.mp4, title=视频4)
 *  onViewRecycled: DyData(url=https://v-cdn.zjol.com.cn/276972.mp4, title=视频1) （回收第1页，注意2/3/4/5还未被回收）
 *  onPageScrollStateChanged: SCROLL_STATE_IDLE, currentItem=6
 *
 * # offscreenPageLimit、onViewRecycled 关系
 *  - 当滑动到第6页时，只有第1页被回收（onViewRecycled 被调用），而其他页面没有被回收。
 *    ViewPager2 内部基于 RecyclerView，会保留一定数量的离屏页面以提升性能，调整缓存大小必须通过 RecyclerView 的相关配置来设置。
 *    offscreenPageLimit 控制的是预加载的页面数量 这些"活页"包含在 RecyclerView 的缓存内。
 *  - 没有调用 onViewRecycled 的界面恢复时应该不会再调用 onBindViewHolder，因为数据可能也没有被冲刷
 *  - (viewPager.getChildAt(0) as RecyclerView).setItemViewCacheSize(3) 影响 onViewRecycled 的调用时机，
 *
 *  # (binding.viewPager2.getChildAt(0) as RecyclerView).layoutManager?.isItemPrefetchEnabled = false
 *  这个设置才会真正控制（是否允许预加载，默认true）：
 *  - 是否提前创建下一页的 ViewHolder（onCreateViewHolder()）
 *  - 是否提前绑定数据（onBindViewHolder()）
 */
class DyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDyBinding
    private val adapter = DyAdapter()
    private val player by lazy {
        val player = ExoPlayer.Builder(this).build()
        player
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected: $position")
                playVideoAt(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val currentItem = binding.viewPager.currentItem
                    Log.d(TAG, "onPageScrollStateChanged: SCROLL_STATE_IDLE, currentItem=$currentItem")
                }
            }
        })
        binding.viewPager.offscreenPageLimit = 1 // 设置预加载页面数量
//        (binding.viewPager.getChildAt(0) as RecyclerView).layoutManager?.isItemPrefetchEnabled = false
        binding.viewPager.adapter = adapter
        adapter.setData(videoList)
        binding.viewPager.setCurrentItem(2, false) // 需要在设置 adapter 之后设置，否则不生效(预加载3,1)
    }

    private var prePlayerView: StyledPlayerView? = null
    private fun playVideoAt(position: Int) {
        prePlayerView?.player = null
        player.stop()
        player.clearVideoSurface()
        val holder = ((binding.viewPager.getChildAt(0) as RecyclerView)
            .findViewHolderForAdapterPosition(position) as? DyViewHolder) ?: return

        holder.binding.playerView.player = player
        prePlayerView = holder.binding.playerView
        val data = videoList[position]
        val mediaItem = MediaItem.fromUri(data.url)
        player.setMediaSource(ProgressiveMediaSource.Factory(adapter.cacheDataSourceFactory).createMediaSource(mediaItem))
        player.prepare()
        player.play()
    }
}