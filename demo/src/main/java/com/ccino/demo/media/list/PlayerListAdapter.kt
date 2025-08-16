package com.ccino.demo.media.list

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.LayoutVideoFeedBinding
import com.ccino.demo.media.VideoInfo


private const val TAG = "PlayerListAdapter"

/**
 *  adapter、holder 不持有 Player, 通过 [ListDetector] 进行播放控制、player 的添加、移除等操作。
 *  每个 holder 是一个 Detector（提供 WrapperPlayerView、videoUrl）
 */
class PlayerListAdapter(private val playDetector: ListDetector) : RecyclerView.Adapter<PlayerListViewHolder>() {
    private val list = mutableListOf<VideoInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
        val binding = LayoutVideoFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerListViewHolder(binding, playDetector)
    }

    override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        val data = list[position]
        holder.bind(data)
    }

    override fun onViewAttachedToWindow(holder: PlayerListViewHolder) {
        Log.d(TAG, "onViewAttachedToWindow: ${holder.binding.root.tag}")
        playDetector.addDetector(holder)
    }

    override fun onViewDetachedFromWindow(holder: PlayerListViewHolder) {
        Log.d(TAG, "onViewDetachedFromWindow: ${holder.binding.root.tag}")
        playDetector.removeDetector(holder)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<VideoInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    //    private val cacheDataSourceFactory: CacheDataSource.Factory = ExoProviders.cacheDataSourceFactory
//    private val cacheDataSource: CacheDataSource = cacheDataSourceFactory.createDataSource()
//
//    fun isFirst1MBCached(cache: Cache, uri: Uri): Boolean {
////        val cacheKey = uri.toString() // 默认 key 规则，你可自定义
//        val dataSpec = DataSpec(uri)
//        val cacheKey = (cacheDataSource.cacheKeyFactory as CustomCacheKeyFactory)
//            .buildCacheKey(dataSpec) // 自定义
//        val start = 0L
//        val length = 1024 * 1024L // 1MB
//
//        val cached = cache.getCachedBytes(cacheKey, start, length)
//        Log.d(TAG, "isFirst1MBCached: cached=$cached, length=$length, uri=$uri")
//        return cached >= length
//    }

    //    private fun cacheVideoSegment(url: String) {
//        val dataSpec = DataSpec.Builder()
//            .setUri(url.toUri())
//            .setPosition(0)
//            .setLength(1024 * 1024)
//            .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
//            .build()
//        // 获取此 URL 实际使用的缓存键：此处只是用于打印
//        val cacheKey = (cacheDataSource.cacheKeyFactory as CustomCacheKeyFactory).buildCacheKey(dataSpec)
//        Log.d(TAG, "cacheVideoSegment: $cacheKey")
//        Thread {
//            CacheWriter(
//                cacheDataSource,
//                dataSpec, null, object : CacheWriter.ProgressListener {
//                    override fun onProgress(requestLength: Long, bytesCached: Long, newBytesCached: Long) {
//                        Log.d(TAG, "onProgress: requestLength=$requestLength, bytesCached=$bytesCached, newBytesCached=$newBytesCached")
//                    }
//
//                }
//            ).cache()
//
//        }.start()
//    }
}
