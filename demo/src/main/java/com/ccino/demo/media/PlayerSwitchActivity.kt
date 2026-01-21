package com.ccino.demo.media

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityPlayerSwitchBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

private const val TAG = "PlayerSwitchActivity"

class PlayerSwitchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerSwitchBinding
    private val player by lazy { ExoPlayer.Builder(this).build() }
    
    // 记录当前播放器位置状态
    private var isInFloatingMode = false
    // 记录是否全屏状态
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerSwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initPlayer()
        initScrollListener()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.toggleFullScreenBtn.setOnClickListener {
            toggleFullScreen()
        }
    }

    private fun initPlayer() {
        // 初始化播放器，绑定到顶部的 playerView
        binding.playerView.player = player

        // 设置视频URL并播放
        val videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    /**
     * 监听滚动事件，控制播放器在普通位置和浮窗位置之间切换
     */
    private fun initScrollListener() {
        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            // 全屏状态下不处理浮窗切换
            if (isFullScreen) return@OnScrollChangeListener
            
            // 获取视频容器的位置信息
            val videoContainerRect = IntArray(2)
            binding.videoContainer.getLocationOnScreen(videoContainerRect)
            val videoContainerTop = videoContainerRect[1]
            val videoContainerHeight = binding.videoContainer.height
            val videoContainerBottom = videoContainerTop + videoContainerHeight
            
            // 计算视频容器可见部分的比例
            val screenTop = 0
            val visibleHeight = when {
                videoContainerBottom <= screenTop -> 0 // 完全不可见
                videoContainerTop >= screenTop && videoContainerBottom <= screenTop + videoContainerHeight -> {
                    // 完全可见
                    videoContainerHeight
                }
                videoContainerTop < screenTop && videoContainerBottom > screenTop -> {
                    // 部分可见（顶部被遮挡）
                    videoContainerBottom - screenTop
                }
                else -> videoContainerHeight
            }
            
            val visibleRatio = visibleHeight.toFloat() / videoContainerHeight
            
            // 当可见部分小于50%时，切换到浮窗模式；大于50%时，切换回普通模式
            if (visibleRatio < 0.5f && !isInFloatingMode) {
                switchToFloatingMode()
            } else if (visibleRatio >= 0.5f && isInFloatingMode) {
                switchToNormalMode()
            }
        })
    }

    /**
     * 切换全屏/非全屏模式
     */
    private fun toggleFullScreen() {
        if (isFullScreen) {
            exitFullScreen()
        } else {
            enterFullScreen()
        }
    }

    /**
     * 进入全屏模式
     */
    private fun enterFullScreen() {
        isFullScreen = true
        
        // 如果当前在浮窗模式，先切回普通模式
        if (isInFloatingMode) {
            switchToNormalMode()
        }
        
        // 隐藏 ScrollView
        binding.scrollView.visibility = View.GONE
        
        // 隐藏浮窗容器（如果可见）
        binding.floatingVideoContainer.visibility = View.GONE
        
        // 从原位置移除 playerView 和按钮
        (binding.playerView.parent as? ViewGroup)?.let { parent ->
            parent.removeView(binding.playerView)
            parent.removeView(binding.toggleFullScreenBtn)
        }
        
        // 创建全屏容器
        val fullScreenContainer = android.widget.FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.BLACK)
        }
        
        // 添加到主布局
        binding.main.addView(fullScreenContainer)
        
        // 添加 playerView 和按钮到全屏容器
        val playerParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        fullScreenContainer.addView(binding.playerView, playerParams)
        
        val btnParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        }
        fullScreenContainer.addView(binding.toggleFullScreenBtn, btnParams)
        
        // 更新按钮文字
        binding.toggleFullScreenBtn.text = "退出"
        
        // 保存全屏容器引用（用于退出时移除）
        fullScreenContainer.tag = "fullScreenContainer"
    }

    /**
     * 退出全屏模式
     */
    private fun exitFullScreen() {
        isFullScreen = false
        
        // 找到全屏容器
        val fullScreenContainer = binding.main.findViewWithTag<ViewGroup>("fullScreenContainer")
        
        // 从全屏容器移除 playerView 和按钮
        fullScreenContainer?.let {
            it.removeView(binding.playerView)
            it.removeView(binding.toggleFullScreenBtn)
            binding.main.removeView(it)
        }
        
        // 添加回原位置
        val playerParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        binding.videoContainer.addView(binding.playerView, playerParams)
        
        val btnParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        }
        binding.videoContainer.addView(binding.toggleFullScreenBtn, btnParams)
        
        // 显示 ScrollView
        binding.scrollView.visibility = View.VISIBLE
        
        // 更新按钮文字
        binding.toggleFullScreenBtn.text = "全屏"
    }

    /**
     * 切换到浮窗模式
     */
    private fun switchToFloatingMode() {
        isInFloatingMode = true
        
        // 从原位置移除 playerView 和按钮
        (binding.playerView.parent as? ViewGroup)?.let { parent ->
            parent.removeView(binding.playerView)
            parent.removeView(binding.toggleFullScreenBtn)
        }
        
        // 显示浮窗容器
        binding.floatingVideoContainer.visibility = View.VISIBLE
        
        // 添加到浮窗容器，使用正确的 LayoutParams
        val playerParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        binding.floatingVideoContainer.addView(binding.playerView, playerParams)
        
        // 添加按钮到浮窗容器
        val btnParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        }
        binding.floatingVideoContainer.addView(binding.toggleFullScreenBtn, btnParams)
    }

    /**
     * 切换回普通模式
     */
    private fun switchToNormalMode() {
        isInFloatingMode = false
        
        // 从浮窗容器移除 playerView 和按钮
        (binding.playerView.parent as? ViewGroup)?.let { parent ->
            parent.removeView(binding.playerView)
            parent.removeView(binding.toggleFullScreenBtn)
        }
        
        // 隐藏浮窗容器
        binding.floatingVideoContainer.visibility = View.GONE
        
        // 添加回原位置，使用正确的 LayoutParams
        val playerParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        binding.videoContainer.addView(binding.playerView, playerParams)
        
        // 添加按钮回原位置
        val btnParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        }
        binding.videoContainer.addView(binding.toggleFullScreenBtn, btnParams)
    }

    /**
     * dp 转 px
     */
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}