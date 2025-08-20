package com.ccino.demo.media.list

import androidx.recyclerview.widget.RecyclerView
import com.ccino.demo.databinding.LayoutVideoFeedBinding
import com.ccino.demo.media.VideoInfo
import com.ccino.demo.util.screenHeight
import com.ccino.demo.util.screenWidth

class PlayerListViewHolder(val binding: LayoutVideoFeedBinding, val playDetector: ListDetector) :
    RecyclerView.ViewHolder(binding.root), ListDetector.IPlayDetector {
    /**
     * 包含 PlayerView、一些其它自定义的播放器 UI 控件
     */
    private val playerView: WrapperPlayerView = binding.playerView
    private var videoUrl: String = ""

    init {
        // 设置播放按钮（暂停、播放）的点击事件监听
        playerView.setListener(object : WrapperPlayerView.PlayBtnListener {
            override fun onTogglePlay(attachView: WrapperPlayerView) {
                playDetector.togglePlay(attachView, attachView.tag as? String ?: "")
            }
        })
        binding.detailBtn.setOnClickListener {
            playDetector.detachPlayer(playerView)
            PlayerDetailActivity.startActivity(this.itemView.context, binding.playerView, binding.root.tag as VideoInfo)
        }
    }

    fun bind(data: VideoInfo, position: Int) {
        binding.root.tag = data
        binding.titleView.text = data.title
        videoUrl = data.url
        binding.playerView.tag = data.url
//        if (position1 == 0)
        binding.root.postDelayed({
            PreloadManager.preload(data.url)
        }, 2000)

        // 根据实际视频宽高比例，设置封面、播放器、模糊背景的宽高
        playerView.bindData(screenWidth, screenHeight / 3, data.cover, data.url, screenHeight / 2)
    }

    override fun getAttachView(): WrapperPlayerView {
        return playerView
    }

    override fun getVideoUrl(): String {
        return videoUrl
    }
}