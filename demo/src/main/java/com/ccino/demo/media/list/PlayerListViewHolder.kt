package com.ccino.demo.media.list

import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.LayoutVideoFeedBinding
import com.ccino.demo.media.VideoInfo
import com.ccino.demo.util.isVisible
import com.ccino.demo.util.screenHeight
import com.ccino.demo.util.screenWidth

class PlayerListViewHolder(val binding: LayoutVideoFeedBinding, val playDetector: PagePlayDetector) : RecyclerView.ViewHolder(binding.root), PagePlayDetector.IPlayDetector {
    private val playerView: WrapperPlayerView = binding.playerView
    private var videoUrl: String = ""
    fun bind(data: VideoInfo) {
        binding.root.tag = data
        binding.titleView.text = data.title
        videoUrl = data.url
        playerView.run {
            isVisible = true
            bindData(screenWidth, screenHeight / 3, data.cover, data.url, screenHeight / 2)
            setListener(object : WrapperPlayerView.Listener {
                override fun onTogglePlay(attachView: WrapperPlayerView) {
                    playDetector.togglePlay(attachView, data.url)
                }
            })
        }
    }

    override fun getAttachView(): WrapperPlayerView {
        return playerView
    }

    override fun getVideoUrl(): String {
        return videoUrl
    }


}