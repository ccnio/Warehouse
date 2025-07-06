package com.ccino.demo.media.cust

import android.content.Context
import android.media.session.PlaybackState
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import coil3.load
import com.ccino.demo.databinding.LayoutListWrapperPlayerViewBinding
import com.ccino.demo.util.DisplayUtil
import com.ccino.demo.util.isVisible
import com.google.android.exoplayer2.Player
import okhttp3.Callback

/**
 * 承载 播放控制器、视频画面
 */
class WrapperPlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
    private lateinit var callback: Listener
    private val binding = LayoutListWrapperPlayerViewBinding.inflate(LayoutInflater.from(context), this)
    init {
        binding.playBtn.setOnClickListener { callback.onTogglePlay(this) }
    }


    fun bindData(width: Int, height: Int, cover: String?, videoUrl: String, maxHeight: Int) {
        // 根据 视频的 width 和 height 动态设置cover、player、blur的宽高
        binding.cover.load(cover)
        binding.blurBackground.isVisible = width < height

        setSize(width, height, DisplayUtil.getScreenWidthPx(context), maxHeight)
    }

    private fun setSize(width: Int, height: Int, maxWidth: Int, maxHeight: Int) {
        val coverHeight: Int
        val coverWidth: Int
        if (width > height) {
            coverWidth = maxWidth
            coverHeight = (maxWidth * height / width.toFloat()).toInt()
        } else {
            coverHeight = maxHeight
            coverWidth = (maxHeight * width / height.toFloat()).toInt()
        }

        val wrapperViewParam = layoutParams
        wrapperViewParam.width = coverWidth
        wrapperViewParam.height = coverHeight
        layoutParams = wrapperViewParam

        val blurParam = binding.blurBackground.layoutParams
        blurParam.width = maxWidth
        blurParam.height = coverHeight
        binding.blurBackground.layoutParams = blurParam

        val coverParam = binding.cover.layoutParams as LayoutParams
        coverParam.width = coverWidth
        coverParam.height = coverHeight
        coverParam.gravity = Gravity.CENTER
        binding.cover.layoutParams = coverParam
    }

    fun onActive(playerView: View, controllerView: View) {
        val parent = playerView.parent
        if (parent != this) {
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
            }
            val coverParam = binding.cover.layoutParams
            this.addView(playerView, 1, coverParam)

        }

        val ctrlParent = controllerView.parent
        if (ctrlParent != this) {
            if (ctrlParent != null) {
                (ctrlParent as ViewGroup).removeView(controllerView)
            }
            val ctrlParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            ctrlParam.gravity = Gravity.BOTTOM
            this.addView(controllerView, ctrlParam)
        }

    }

    fun inActive() {
        binding.cover.isVisible = true
        binding.playBtn.isVisible = true
        binding.playBtn.setImageResource(android.R.drawable.ic_media_play)
    }

    fun onPlayerStateChanged(playing: Boolean, playbackState: Int) {
        if (playing) {
            binding.cover.isVisible = false
            binding.bufferView.isVisible = false
            binding.playBtn.isVisible = true
            binding.playBtn.setImageResource(android.R.drawable.ic_media_pause)
        } else if (playbackState == Player.STATE_ENDED) {
            binding.cover.isVisible = true
            binding.playBtn.isVisible = true
            binding.playBtn.setImageResource(android.R.drawable.ic_media_play)
        } else if( playbackState == Player.STATE_BUFFERING) {
            binding.bufferView.isVisible = true
        }
    }

    fun onVisibilityChanged(visibility: Int, end: Boolean) {
        binding.playBtn.isVisible = visibility == VISIBLE || end
    }

    fun setListener(callback: Listener) {
        this.callback = callback
    }
    interface Listener {
        fun onTogglePlay(attachView: WrapperPlayerView)
    }
}