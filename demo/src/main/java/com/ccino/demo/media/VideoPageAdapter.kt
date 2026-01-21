package com.ccino.demo.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.R

/**
 * ViewPager2 视频页面适配器
 */
class VideoPageAdapter : RecyclerView.Adapter<VideoPageAdapter.VideoPageViewHolder>() {
    
    private val videoList = mutableListOf<VideoItem>()
    
    fun setData(list: List<VideoItem>) {
        videoList.clear()
        videoList.addAll(list)
        notifyDataSetChanged()
    }
    
    fun getVideoUrl(position: Int): String? {
        return videoList.getOrNull(position)?.videoUrl
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_page, parent, false)
        return VideoPageViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: VideoPageViewHolder, position: Int) {
        val item = videoList[position]
        holder.bind(item, position)
    }
    
    override fun getItemCount(): Int = videoList.size
    
    /**
     * ViewHolder
     */
    class VideoPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerContainer: FrameLayout = itemView.findViewById(R.id.playerContainer)
        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val loadingProgress: ProgressBar = itemView.findViewById(R.id.loadingProgress)
        private val usernameText: TextView = itemView.findViewById(R.id.usernameText)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val musicText: TextView = itemView.findViewById(R.id.musicText)
        
        fun bind(item: VideoItem, position: Int) {
            usernameText.text = "@用户${position + 1}"
            titleText.text = item.title
            musicText.text = "♬ 原声 - ${item.description}"
            
            // 保存视频信息到tag
            itemView.tag = item
            
            // 清空播放器容器
            playerContainer.removeAllViews()
        }
        
        /**
         * 获取播放器容器
         */
        fun getPlayerContainer(): ViewGroup = playerContainer
        
        /**
         * 显示封面
         */
        fun showCover() {
            coverImage.visibility = View.VISIBLE
        }
        
        /**
         * 隐藏封面
         */
        fun hideCover() {
            coverImage.visibility = View.GONE
        }
        
        /**
         * 显示加载进度
         */
        fun showLoading() {
            loadingProgress.visibility = View.VISIBLE
        }
        
        /**
         * 隐藏加载进度
         */
        fun hideLoading() {
            loadingProgress.visibility = View.GONE
        }
    }
}
