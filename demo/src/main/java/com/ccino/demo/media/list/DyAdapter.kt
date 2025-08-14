package com.ccino.demo.media.list

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.app
import com.ccino.demo.databinding.DyLayoutVideoBinding
import com.ccino.demo.media.VideoInfo
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


private const val TAG = "DyAdapter"

class DyAdapter : RecyclerView.Adapter<DyViewHolder>() {
    private val list = mutableListOf<VideoInfo>()
    var cacheDataSource: CacheDataSource
    var cacheDataSourceFactory: CacheDataSource.Factory
    val cache by lazy {
        SimpleCache(
            File(app.externalCacheDir, "exo_cache"),
            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024)
        ) // 1024MB max size
    }

    init {

        val cacheKeyFactory = CustomCacheKeyFactory()
        // 1. 创建CacheDataSource
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache) // 设置缓存实例
            .setCacheKeyFactory(cacheKeyFactory) // 设置自定义缓存键工厂
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory()) // 设置网络数据源
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR) // 缓存出错时回退到网络
        cacheDataSource = cacheDataSourceFactory.createDataSource()
    }

    fun isFirst1MBCached(cache: Cache, uri: Uri): Boolean {
//        val cacheKey = uri.toString() // 默认 key 规则，你可自定义
        val dataSpec = DataSpec(uri)
        val cacheKey = (cacheDataSource.cacheKeyFactory as CustomCacheKeyFactory)
            .buildCacheKey(dataSpec) // 自定义
        val start = 0L
        val length = 1024 * 1024L // 1MB

        val cached = cache.getCachedBytes(cacheKey, start, length)
        Log.d(TAG, "isFirst1MBCached: cached=$cached, length=$length, uri=$uri")
        return cached >= length
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DyViewHolder {
        val binding = DyLayoutVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DyViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        val data = list[position]
        holder.bind(data)
        // 这里不直接播放，交由 Activity/Fragment 控制
        if (position == 3) {
            val l = System.currentTimeMillis()
            isFirst1MBCached(cache, data.url.toUri())
            Log.d(TAG, "onBindViewHolder: isFirstConsume=${System.currentTimeMillis() - l}ms, url=${data.url}")
            cacheVideoSegment(data.url)
        }
    }

    private fun cacheVideoSegment(url: String) {
        val dataSpec = DataSpec.Builder()
            .setUri(url.toUri())
            .setPosition(0)
            .setLength(1024 * 1024)
            .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
            .build()
        // 获取此 URL 实际使用的缓存键：此处只是用于打印
        val cacheKey = (cacheDataSource.cacheKeyFactory as CustomCacheKeyFactory).buildCacheKey(dataSpec)
        Log.d(TAG, "cacheVideoSegment: $cacheKey")
        Thread {
            CacheWriter(
                cacheDataSource,
                dataSpec, null, object : CacheWriter.ProgressListener {
                    override fun onProgress(requestLength: Long, bytesCached: Long, newBytesCached: Long) {
                        Log.d(TAG, "onProgress: requestLength=$requestLength, bytesCached=$bytesCached, newBytesCached=$newBytesCached")
                    }

                }
            ).cache()

        }.start()
    }


    override fun onViewAttachedToWindow(holder: DyViewHolder) {
        Log.d(TAG, "onViewAttachedToWindow: ${holder.binding.root.tag}")
    }

    override fun onViewDetachedFromWindow(holder: DyViewHolder) {
        Log.d(TAG, "onViewDetachedFromWindow: ${holder.binding.root.tag}")
    }

    override fun onViewRecycled(holder: DyViewHolder) {
        Log.d(TAG, "onViewRecycled: ${holder.binding.root.tag}")
    }

    override fun getItemCount() = list.size
    fun setData(list: MutableList<VideoInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}

class DyViewHolder(val binding: DyLayoutVideoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: VideoInfo) {
        binding.root.tag = data
        binding.titleView.text = data.title
    }


}
