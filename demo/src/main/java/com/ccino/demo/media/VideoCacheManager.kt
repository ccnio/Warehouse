package com.ccino.demo.media

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

/**
 * 视频缓存管理器 - 单例模式
 * 管理视频的本地缓存
 */
object VideoCacheManager {
    
    private const val CACHE_DIR_NAME = "video_cache"
    private const val MAX_CACHE_SIZE = 200L * 1024 * 1024 // 200MB
    
    private var cache: SimpleCache? = null
    private var cacheDataSourceFactory: DataSource.Factory? = null
    
    /**
     * 初始化缓存
     */
    fun initialize(context: Context) {
        if (cache == null) {
            val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
            val databaseProvider = StandaloneDatabaseProvider(context)
            
            // 使用 LRU 策略，最大缓存 200MB
            cache = SimpleCache(
                cacheDir,
                LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE),
                databaseProvider
            )
            
            // 创建支持缓存的 DataSource.Factory
            val upstreamFactory = DefaultDataSource.Factory(
                context,
                DefaultHttpDataSource.Factory()
                    .setUserAgent("ExoPlayer")
                    .setConnectTimeoutMs(10000)
                    .setReadTimeoutMs(10000)
            )
            
            cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(cache!!)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setCacheWriteDataSinkFactory(null) // 使用默认写入
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }
    }
    
    /**
     * 获取支持缓存的 DataSource.Factory
     */
    fun getCacheDataSourceFactory(): DataSource.Factory {
        return cacheDataSourceFactory 
            ?: throw IllegalStateException("VideoCacheManager not initialized")
    }
    
    /**
     * 获取 Cache 实例
     */
    fun getCache(): Cache {
        return cache ?: throw IllegalStateException("VideoCacheManager not initialized")
    }
    
    /**
     * 检查视频是否已缓存
     */
    fun isCached(videoUrl: String): Boolean {
        return cache?.isCached(videoUrl, 0, 10 * 1024 * 1024) ?: false // 检查前10MB
    }
    
    /**
     * 清空缓存
     */
    fun clearCache() {
        cache?.release()
        cache = null
        cacheDataSourceFactory = null
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(): Long {
        return cache?.cacheSpace ?: 0
    }
}
