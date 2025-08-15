package com.ccino.demo.media.list

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.DyLayoutVideoBinding
import com.ccino.demo.media.VideoInfo
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter


private const val TAG = "DyAdapter"

class DyAdapter : RecyclerView.Adapter<DyViewHolder>() {
    private val list = mutableListOf<VideoInfo>()
    var cacheDataSource: CacheDataSource
    var cacheDataSourceFactory: CacheDataSource.Factory

    init {
        cacheDataSourceFactory = ExoProviders.cacheDataSourceFactory
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
        if (position == 3) {
            val l = System.currentTimeMillis()
            isFirst1MBCached(ExoProviders.simpleCache, data.url.toUri())
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
