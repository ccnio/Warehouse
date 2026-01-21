package com.ccino.demo.media

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView

/**
 * 视频播放器管理类 - 单例模式
 * 管理全局唯一的播放器实例，实现播放器的复用和状态管理
 */
object VideoPlayerManager {
    
    private const val TAG = "VideoPlayerManager"
    
    private var player: ExoPlayer? = null
    private var currentPlayerView: StyledPlayerView? = null
    private var currentPlayingPosition: Int = -1
    private var currentPlayingUrl: String? = null
    
    /**
     * 初始化播放器
     */
    fun initialize(context: Context) {
        if (player == null) {
            // 使用支持缓存的 MediaSourceFactory
            player = ExoPlayer.Builder(context.applicationContext)
                .setMediaSourceFactory(
                    ProgressiveMediaSource.Factory(
                        VideoCacheManager.getCacheDataSourceFactory()
                    )
                )
                .build().apply {
                    // 设置循环播放
                    repeatMode = Player.REPEAT_MODE_ONE
                    // 设置音量
                    volume = 1f
                    
                    // 监听播放状态
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_ENDED -> {
                                    // 播放结束，标记为已缓存
                                    currentPlayingUrl?.let {
                                        VideoPreloadManager.markAsCached(it)
                                        Log.d(TAG, "Video playback ended, marked as cached: $it")
                                    }
                                }
                            }
                        }
                    })
                }
        }
    }
    
    /**
     * 播放指定位置的视频
     * @param container 播放器容器
     * @param videoUrl 视频URL
     * @param position item位置
     */
    fun play(container: ViewGroup, videoUrl: String, position: Int) {
        // 如果正在播放同一个位置，不做处理
        if (currentPlayingPosition == position && player?.isPlaying == true) {
            return
        }
        
        val exoPlayer = player ?: return
        
        // 从旧容器移除播放器视图
        detachPlayerView()
        
        // 创建或复用PlayerView
        val playerView = currentPlayerView ?: StyledPlayerView(container.context).also {
            it.player = exoPlayer
            it.useController = false // 不显示控制器，可根据需求调整
            currentPlayerView = it
        }
        
        // 添加到新容器
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        container.addView(playerView, layoutParams)
        
        // 设置视频源并播放（使用缓存）
        val mediaItem = MediaItem.fromUri(videoUrl)
        val mediaSource = ProgressiveMediaSource.Factory(
            VideoCacheManager.getCacheDataSourceFactory()
        ).createMediaSource(mediaItem)
        
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        
        currentPlayingPosition = position
        currentPlayingUrl = videoUrl
        
        Log.d(TAG, "Playing video at position $position: $videoUrl")
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        player?.pause()
    }
    
    /**
     * 恢复播放
     */
    fun resume() {
        player?.play()
    }
    
    /**
     * 停止播放并移除播放器视图
     */
    fun stop() {
        player?.stop()
        detachPlayerView()
        currentPlayingPosition = -1
    }
    
    /**
     * 从容器中移除播放器视图
     */
    private fun detachPlayerView() {
        currentPlayerView?.let { playerView ->
            (playerView.parent as? ViewGroup)?.removeView(playerView)
        }
    }
    
    /**
     * 获取当前播放位置
     */
    fun getCurrentPosition(): Int = currentPlayingPosition
    
    /**
     * 释放播放器资源
     */
    fun release() {
        detachPlayerView()
        player?.release()
        player = null
        currentPlayerView = null
        currentPlayingPosition = -1
    }
    
    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean = player?.isPlaying == true
}
