package com.ccino.demo.media

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * 视频预加载管理器 - 单例模式
 * 负责预加载视频的前10秒
 */
@SuppressLint("StaticFieldLeak")
object VideoPreloadManager {

    private const val TAG = "VideoPreloadManager"
    private const val PRELOAD_DURATION_MS = 10_000L // 预加载10秒

    private var context: Context? = null
    private var preloadPlayer: ExoPlayer? = null

    // 预加载任务管理
    private val preloadJobs = ConcurrentHashMap<String, Job>()

    // 记录已预加载的视频
    private val preloadedVideos = ConcurrentHashMap<String, Boolean>()

    // 记录已完全缓存的视频（播放过的）
    private val cachedVideos = ConcurrentHashMap<String, Boolean>()

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * 初始化预加载管理器
     */
    fun initialize(context: Context) {
        this.context = context.applicationContext

        // 创建预加载专用的播放器
        if (preloadPlayer == null) {
            preloadPlayer = ExoPlayer.Builder(context.applicationContext)
                .setMediaSourceFactory(
                    ProgressiveMediaSource.Factory(
                        VideoCacheManager.getCacheDataSourceFactory()
                    )
                )
                .build().apply {
                    volume = 0f // 静音
                    playWhenReady = false

                    // 监听播放器状态
                    addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e(TAG, "Preload player error: ${error.message}")
                        }
                    })
                }
        }
    }

    /**
     * 预加载视频
     * @param videoUrl 视频URL
     * @param priority 优先级（高优先级会取消低优先级的预加载）
     */
    fun preload(videoUrl: String, priority: Int = 0) {
        // 如果已经预加载过或已缓存，跳过
        if (preloadedVideos.containsKey(videoUrl) || cachedVideos.containsKey(videoUrl)) {
            Log.d(TAG, "Video already preloaded or cached: $videoUrl")
            return
        }

        // 取消已有的预加载任务
        preloadJobs[videoUrl]?.cancel()

        // 创建新的预加载任务
        val job = scope.launch {
            try {
                Log.d(TAG, "Start preloading: $videoUrl")

                val player = preloadPlayer ?: return@launch
                val mediaItem = MediaItem.fromUri(videoUrl)
                val mediaSource = ProgressiveMediaSource.Factory(
                    VideoCacheManager.getCacheDataSourceFactory()
                ).createMediaSource(mediaItem)

                // 设置媒体源
                player.setMediaSource(mediaSource)
                player.prepare()

                // 等待准备完成
                var waitTime = 0L
                while (player.playbackState == Player.STATE_BUFFERING && isActive && waitTime < 30_000) {
                    delay(100)
                    waitTime += 100
                }

                if (!isActive) {
                    Log.d(TAG, "Preload cancelled: $videoUrl")
                    return@launch
                }

                if (player.playbackState == Player.STATE_READY) {
                    // 开始"播放"（实际是缓存）
                    player.play()

                    // 等待缓存10秒的数据
                    var cachedTime = 0L
                    while (cachedTime < PRELOAD_DURATION_MS && isActive) {
                        delay(100)
                        cachedTime += 100

                        // 检查缓冲进度
                        val bufferedPosition = player.bufferedPosition
                        if (bufferedPosition >= PRELOAD_DURATION_MS) {
                            break
                        }
                    }

                    // 停止预加载
                    player.pause()
                    player.stop()

                    if (isActive) {
                        preloadedVideos[videoUrl] = true
                        Log.d(TAG, "Preload completed: $videoUrl")
                    }
                } else {
                    Log.w(TAG, "Preload failed, player not ready: $videoUrl")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Preload error: $videoUrl", e)
            } finally {
                preloadJobs.remove(videoUrl)
            }
        }

        preloadJobs[videoUrl] = job
    }

    /**
     * 预加载多个视频
     * @param urls 视频URL列表
     */
    fun preloadVideos(urls: List<String>) {
        urls.forEachIndexed { index, url ->
            // 延迟预加载，避免同时发起太多请求
            scope.launch {
                delay(index * 500L)
                preload(url, priority = urls.size - index)
            }
        }
    }

    /**
     * 取消预加载
     */
    fun cancelPreload(videoUrl: String) {
        preloadJobs[videoUrl]?.cancel()
        preloadJobs.remove(videoUrl)
    }

    /**
     * 取消所有预加载
     */
    fun cancelAllPreload() {
        preloadJobs.values.forEach { it.cancel() }
        preloadJobs.clear()
    }

    /**
     * 标记视频为已完全缓存（播放过的视频）
     */
    fun markAsCached(videoUrl: String) {
        cachedVideos[videoUrl] = true
        preloadedVideos.remove(videoUrl)
        Log.d(TAG, "Video marked as cached: $videoUrl")
    }

    /**
     * 检查视频是否已预加载
     */
    fun isPreloaded(videoUrl: String): Boolean {
        return preloadedVideos.containsKey(videoUrl) || cachedVideos.containsKey(videoUrl)
    }

    /**
     * 获取预加载状态
     */
    fun getPreloadStatus(): String {
        return "Preloaded: ${preloadedVideos.size}, Cached: ${cachedVideos.size}, Preloading: ${preloadJobs.size}"
    }

    /**
     * 释放资源
     */
    fun release() {
        cancelAllPreload()
        preloadPlayer?.release()
        preloadPlayer = null
        preloadedVideos.clear()
        cachedVideos.clear()
    }
}
