package com.ccino.demo.media.list

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.app
import com.ccino.demo.databinding.LayoutVideoFeedBinding
import com.ccino.demo.media.VideoInfo
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


private const val TAG = "PlayerListAdapter"

class PlayerListAdapter(private val pageName: String, private val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<PlayerListViewHolder>() {
    private lateinit var playDetector: PagePlayDetector
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
        val binding = LayoutVideoFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerListViewHolder(binding, playDetector)
    }

    override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        val data = list[position]
        holder.bind(data)
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


    override fun onViewAttachedToWindow(holder: PlayerListViewHolder) {
        Log.d(TAG, "onViewAttachedToWindow: ${holder.binding.root.tag}")
        playDetector.addDetector(holder)
    }


    override fun onViewDetachedFromWindow(holder: PlayerListViewHolder) {
        Log.d(TAG, "onViewDetachedFromWindow: ${holder.binding.root.tag}")
        playDetector.removeDetector(holder)
    }

    override fun onViewRecycled(holder: PlayerListViewHolder) {
        Log.d(TAG, "onViewRecycled: ${holder.binding.root.tag}")
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        playDetector = PagePlayDetector(pageName, lifecycleOwner, recyclerView)
    }

    override fun getItemCount() = list.size
    fun setData(list: List<VideoInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}
