package com.ccino.demo.media

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ccino.demo.databinding.ActivityPlayerPageBinding

/**
 * 类似抖音的全屏视频滑动播放Activity
 * 使用ViewPager2实现垂直滑动
 */
class PlayerPageActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlayerPageBinding
    private lateinit var adapter: VideoPageAdapter
    
    // 当前播放的页面位置
    private var currentPosition = 0
    
    // 是否正在滑动
    private var isScrolling = false
    
    companion object {
        private const val TAG = "PlayerPageActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化缓存
        VideoCacheManager.initialize(this)
        
        // 初始化播放器
        VideoPlayerManager.initialize(this)
        
        // 初始化预加载管理器
        VideoPreloadManager.initialize(this)
        
        initViewPager()
        loadMockData()
    }
    
    /**
     * 初始化ViewPager2
     */
    private fun initViewPager() {
        adapter = VideoPageAdapter()
        
        binding.viewPager.apply {
            adapter = this@PlayerPageActivity.adapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            
            // 设置预加载页面数
            offscreenPageLimit = 1 // 预加载前后各1页
            
            // 添加页面切换监听
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            // 滑动停止
                            isScrolling = false
                            Log.d(TAG, "Page scroll IDLE, current position: $currentPosition")
                        }
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            // 开始拖动
                            isScrolling = true
                            Log.d(TAG, "Page scroll DRAGGING")
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {
                            // 自动滚动到固定位置
                            isScrolling = true
                            Log.d(TAG, "Page scroll SETTLING")
                        }
                    }
                }
                
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d(TAG, "Page selected: $position")
                    
                    // 更新当前位置
                    currentPosition = position
                    
                    // 播放当前页面视频
                    playCurrentPage()
                    
                    // 预加载相邻页面
                    preloadAdjacentPages(position)
                }
            })
        }
    }
    
    /**
     * 播放当前页面的视频
     */
    private fun playCurrentPage() {
        val recyclerView = binding.viewPager.getChildAt(0) as? RecyclerView ?: return
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(currentPosition) 
            as? VideoPageAdapter.VideoPageViewHolder ?: return
        
        // 获取视频信息
        val videoItem = viewHolder.itemView.tag as? VideoItem ?: return
        
        // 获取播放器容器
        val container = viewHolder.getPlayerContainer()
        
        // 隐藏封面，显示加载进度
        viewHolder.hideCover()
        viewHolder.showLoading()
        
        // 开始播放
        VideoPlayerManager.play(container, videoItem.videoUrl, currentPosition)
        
        // 播放开始后隐藏加载进度
        container.postDelayed({
            viewHolder.hideLoading()
        }, 500)
        
        Log.d(TAG, "Playing video at position $currentPosition: ${videoItem.videoUrl}")
    }
    
    /**
     * 预加载相邻页面的视频
     */
    private fun preloadAdjacentPages(position: Int) {
        val preloadUrls = mutableListOf<String>()
        
        // 预加载下一页
        adapter.getVideoUrl(position + 1)?.let { url ->
            if (!VideoPreloadManager.isPreloaded(url)) {
                preloadUrls.add(url)
            }
        }
        
        // 预加载上一页
        adapter.getVideoUrl(position - 1)?.let { url ->
            if (!VideoPreloadManager.isPreloaded(url)) {
                preloadUrls.add(url)
            }
        }
        
        // 预加载下下页（提前准备）
        adapter.getVideoUrl(position + 2)?.let { url ->
            if (!VideoPreloadManager.isPreloaded(url)) {
                preloadUrls.add(url)
            }
        }
        
        // 异步预加载
        if (preloadUrls.isNotEmpty()) {
            VideoPreloadManager.preloadVideos(preloadUrls)
            Log.d(TAG, "Preloading ${preloadUrls.size} videos")
        }
    }
    
    /**
     * 加载模拟数据
     */
    private fun loadMockData() {
        val videoList = listOf(
            VideoItem(
                id = "1",
                title = "这是第一个视频 #热门 #推荐",
                description = "精彩内容1",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            VideoItem(
                id = "2",
                title = "这是第二个视频的标题，可以很长很长的内容展示 #搞笑 #日常",
                description = "精彩内容2",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            ),
            VideoItem(
                id = "3",
                title = "第三个视频来了 #美食 #教程",
                description = "精彩内容3",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            VideoItem(
                id = "4",
                title = "超级精彩的第四个视频 #旅行 #Vlog",
                description = "精彩内容4",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
            ),
            VideoItem(
                id = "5",
                title = "第五个视频 #汽车 #测评",
                description = "精彩内容5",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
            ),
            VideoItem(
                id = "6",
                title = "第六个视频内容 #科技 #数码",
                description = "精彩内容6",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            ),
            VideoItem(
                id = "7",
                title = "第七个精彩视频 #运动 #健身",
                description = "精彩内容7",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4"
            ),
            VideoItem(
                id = "8",
                title = "最后一个视频啦 #音乐 #舞蹈",
                description = "精彩内容8",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
            )
        )
        
        adapter.setData(videoList)
        
        // 数据加载完成后，延迟播放第一个视频
        binding.viewPager.post {
            playCurrentPage()
            preloadAdjacentPages(0)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // 恢复播放
        if (!isScrolling) {
            VideoPlayerManager.resume()
        }
    }
    
    override fun onPause() {
        super.onPause()
        // 暂停播放
        VideoPlayerManager.pause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 释放播放器资源
        VideoPlayerManager.release()
        // 释放预加载资源
        VideoPreloadManager.release()
    }
}
