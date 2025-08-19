package com.ccino.demo.media.list

import android.util.Log
import androidx.core.net.toUri
import com.ccino.demo.app
import com.ccino.demo.media.cust.CustomCacheKeyFactory
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

private const val TAG = "PreLoadManager"
private const val PRELOAD_LENGTH = 5 * 1024 * 1024L //1M

object PreloadManager {
    private val fixedThreadPool = Executors.newFixedThreadPool(2)
    private val scope = CoroutineScope(fixedThreadPool.asCoroutineDispatcher())
    private val appContext = app
    val cacheDir = File(appContext.externalCacheDir, "media_cache")

    private val simpleCache: SimpleCache by lazy {
        val evictor = LeastRecentlyUsedCacheEvictor(200L * 1024 * 1024) // 200MB
        SimpleCache(cacheDir, evictor, StandaloneDatabaseProvider(appContext))
    }

    private val upstreamFactory = DefaultHttpDataSource.Factory()
    private val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCacheKeyFactory(CustomCacheKeyFactory())
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(upstreamFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    private val preloadTasks = mutableMapOf<String, Job>() // 记录已启动的预加载任务

    fun getCacheFactory(): DataSource.Factory {
        return cacheDataSourceFactory
    }

    /**
     * 预加载一个视频
     */
    fun preload(url: String) {
        if (isCached(url)) return
        if (preloadTasks.containsKey(url)) return

        val job = scope.launch {
            runCatching {
                val dataSpec = DataSpec.Builder()
                    .setUri(url.toUri())
                    .setPosition(0) //设置从文件的哪个字节位置开始读取
                    .setLength(PRELOAD_LENGTH) //设置要读取的数据长度 1M
                    .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION) //FLAG_ALLOW_CACHE_FRAGMENTATION：允许缓存不连续的数据块
                    .build()

                val writer = CacheWriter(cacheDataSourceFactory.createDataSource(), dataSpec, null) { requestLength, bytesCached, newBytesCached ->
//                val cacheKey = (cacheDataSourceFactory.cacheKeyFactory as CustomCacheKeyFactory).buildCacheKey(dataSpec)
                    // requestLength: 要缓存的总长度
                    // bytesCached: 已经缓存的长度
                    // newBytesCached: 最近一次缓存的长度
//                Log.d(TAG, "onProgress: requestLength=$requestLength, bytesCached=$bytesCached, newBytesCached=$newBytesCached,key=$cacheKey")
                }
                writer.cache()
                preloadTasks.remove(url)
            }.onFailure {
                preloadTasks.remove(url) //err=java.net.UnknownHostException:
                Log.d(TAG, "preload: err=${it.message}")
            }
        }
        preloadTasks[url] = job
    }

    fun isCached(url: String): Boolean {
        val l = System.currentTimeMillis()
        //        val cacheKey = uri.toString() // 默认 key 规则，你可自定义
        val dataSpec = DataSpec.Builder()
            .setUri(url.toUri())
            .build()
        val cacheKey = (cacheDataSourceFactory.cacheKeyFactory as CustomCacheKeyFactory).buildCacheKey(dataSpec) // 自定义
        val start = 0L

        val cachedLen = simpleCache.getCachedBytes(cacheKey, start, PRELOAD_LENGTH)
        val cached = cachedLen >= PRELOAD_LENGTH
        Log.d(TAG, "isFirst1MBCached: cached=${cached}, cachedLen=$cachedLen, uri=$url, key=$cacheKey, consume=${System.currentTimeMillis() - l}")
        return cached
    }


    /**
     * 取消某个视频的预加载
     */
    fun cancel(url: String) {
        preloadTasks[url]?.cancel()
        preloadTasks.remove(url)
    }

    /**
     * 清空所有预加载任务
     */
    fun cancelAll() {
        preloadTasks.values.forEach { it.cancel() }
        preloadTasks.clear()
    }

    fun release() {
        cancelAll()
        simpleCache.release()
    }
}
