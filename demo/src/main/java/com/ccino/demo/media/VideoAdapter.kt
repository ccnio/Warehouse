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
 * 视频列表适配器
 */
class VideoAdapter(
    private val itemHeight: Int
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    
    private val videoList = mutableListOf<VideoItem>()
    
    fun setData(list: List<VideoItem>) {
        videoList.clear()
        videoList.addAll(list)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        
        // 设置item高度为屏幕高度的1/3
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            itemHeight
        )
        
        return VideoViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = videoList[position]
        holder.bind(item, position)
    }
    
    override fun getItemCount(): Int = videoList.size
    
    /**
     * ViewHolder
     */
    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerContainer: FrameLayout = itemView.findViewById(R.id.playerContainer)
        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val loadingProgress: ProgressBar = itemView.findViewById(R.id.loadingProgress)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val descText: TextView = itemView.findViewById(R.id.descText)
        
        fun bind(item: VideoItem, position: Int) {
            titleText.text = item.title
            descText.text = item.description
            
            // 保存视频URL到tag中，供播放器使用
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
