package com.ccino.demo.media

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.ActivityPlayerListBinding

/**
 * 视频列表播放Activity
 * 实现业界标准的短视频列表播放方案
 */
class PlayerListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlayerListBinding
    private lateinit var adapter: VideoAdapter
    private var itemHeight: Int = 0
    
    // 是否正在滚动
    private var isScrolling = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化缓存
        VideoCacheManager.initialize(this)
        
        // 初始化播放器
        VideoPlayerManager.initialize(this)
        
        // 初始化预加载管理器
        VideoPreloadManager.initialize(this)
        
        // 计算item高度（屏幕高度的1/3）
        itemHeight = getScreenHeight() / 3
        
        initRecyclerView()
        loadMockData()
    }
    
    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        adapter = VideoAdapter(itemHeight)
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PlayerListActivity)
            adapter = this@PlayerListActivity.adapter
            
            // 添加滚动监听
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            // 滚动停止，开始播放
                            isScrolling = false
                            autoPlayVideo()
                        }
                        RecyclerView.SCROLL_STATE_DRAGGING,
                        RecyclerView.SCROLL_STATE_SETTLING -> {
                            // 正在滚动
                            isScrolling = true
                        }
                    }
                }
                
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    // 滚动过程中实时检查，提升响应速度
                    if (!isScrolling) {
                        autoPlayVideo()
                    }
                }
            })
        }
    }
    
    /**
     * 自动播放视频
     * 找到第一个可见高度大于50%的item并播放
     */
    private fun autoPlayVideo() {
        val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager ?: return
        
        // 获取当前可见的第一个和最后一个item位置
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        
        if (firstVisiblePosition == RecyclerView.NO_POSITION) {
            return
        }
        
        // 遍历可见的item，找到第一个可见度>50%的
        for (position in firstVisiblePosition..lastVisiblePosition) {
            val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(position) 
                as? VideoAdapter.VideoViewHolder ?: continue
            
            val visiblePercent = calculateVisiblePercent(viewHolder)
            
            // 找到第一个可见度大于50%的item
            if (visiblePercent > 0.5f) {
                playVideoAtPosition(position, viewHolder)
                // 触发预加载
                preloadNextVideos(position)
                return
            }
        }
    }
    
    /**
     * 预加载下一个和下下个视频
     * @param currentPosition 当前播放位置
     */
    private fun preloadNextVideos(currentPosition: Int) {
        val preloadUrls = mutableListOf<String>()
        
        // 预加载接下来的2-3个视频
        for (i in 1..3) {
            val nextPosition = currentPosition + i
            val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(nextPosition) 
                as? VideoAdapter.VideoViewHolder
            
            val videoItem = viewHolder?.itemView?.tag as? VideoItem
            videoItem?.let {
                if (!VideoPreloadManager.isPreloaded(it.videoUrl)) {
                    preloadUrls.add(it.videoUrl)
                }
            }
        }
        
        // 异步预加载
        if (preloadUrls.isNotEmpty()) {
            VideoPreloadManager.preloadVideos(preloadUrls)
        }
    }
    
    /**
     * 计算item的可见百分比
     */
    private fun calculateVisiblePercent(viewHolder: VideoAdapter.VideoViewHolder): Float {
        val itemView = viewHolder.itemView
        val itemRect = android.graphics.Rect()
        
        // 获取item在屏幕中的位置
        if (!itemView.getGlobalVisibleRect(itemRect)) {
            return 0f
        }
        
        val itemHeight = itemView.height
        val visibleHeight = itemRect.height()
        
        return if (itemHeight > 0) {
            visibleHeight.toFloat() / itemHeight.toFloat()
        } else {
            0f
        }
    }
    
    /**
     * 播放指定位置的视频
     */
    private fun playVideoAtPosition(position: Int, viewHolder: VideoAdapter.VideoViewHolder) {
        // 如果已经在播放这个位置，不重复播放
        if (VideoPlayerManager.getCurrentPosition() == position) {
            return
        }
        
        // 获取视频数据
        val videoItem = viewHolder.itemView.tag as? VideoItem ?: return
        
        // 获取播放器容器
        val container = viewHolder.getPlayerContainer()
        
        // 隐藏封面，显示加载进度
        viewHolder.hideCover()
        viewHolder.showLoading()
        
        // 开始播放
        VideoPlayerManager.play(container, videoItem.videoUrl, position)
        
        // 播放开始后隐藏加载进度（实际项目中应该监听播放器状态）
        container.postDelayed({
            viewHolder.hideLoading()
        }, 500)
    }
    
    /**
     * 加载模拟数据
     */
    private fun loadMockData() {
        val videoList = listOf(
            VideoItem(
                id = "1",
                title = "视频 1",
                description = "这是第一个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            VideoItem(
                id = "2",
                title = "视频 2",
                description = "这是第二个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            ),
            VideoItem(
                id = "3",
                title = "视频 3",
                description = "这是第三个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            VideoItem(
                id = "4",
                title = "视频 4",
                description = "这是第四个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
            ),
            VideoItem(
                id = "5",
                title = "视频 5",
                description = "这是第五个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
            ),
            VideoItem(
                id = "6",
                title = "视频 6",
                description = "这是第六个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            ),
            VideoItem(
                id = "7",
                title = "视频 7",
                description = "这是第七个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4"
            ),
            VideoItem(
                id = "8",
                title = "视频 8",
                description = "这是第八个视频的描述",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
            )
        )
        
        adapter.setData(videoList)
        
        // 数据加载完成后，自动播放第一个视频
        binding.recyclerView.post {
            autoPlayVideo()
        }
    }
    
    /**
     * 获取屏幕高度
     */
    private fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
    
    override fun onResume() {
        super.onResume()
        // 恢复播放
        VideoPlayerManager.resume()
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
