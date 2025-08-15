package com.ccino.demo.media.list

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.LayoutVideoFeedBinding
import com.ccino.demo.media.VideoInfo
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter


private const val TAG = "PlayerListAdapter"

class PlayerListAdapter(private val pageName: String, private val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<PlayerListViewHolder>() {
    private lateinit var playDetector: PagePlayDetector
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
        // 当第一个可见的 ViewHolder 附着时，主动请求自动播放
        if (holder.bindingAdapterPosition == 0) {
            holder.itemView.post { playDetector.requestAutoPlay() }
        }
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
        recyclerView.post {
            // 兜底：列表渲染完成后再触发一次自动播放检测
            playDetector.requestAutoPlay()
        }
    }

    override fun getItemCount() = list.size
    fun setData(list: List<VideoInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}
