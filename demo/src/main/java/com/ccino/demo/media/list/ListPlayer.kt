package com.ccino.demo.media.list

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import androidx.core.net.toUri
import com.ccino.demo.R
import com.ccino.demo.app
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView

private const val TAG = "PageListPlayer"

class ListPlayer : IListPlayer, Player.Listener, StyledPlayerControlView.VisibilityListener {
    /**
     * 一个列表对应一个 Player 实例，一个 exoPlayerView，每个 item 有 WrapperPlayerView。
     * WrapperPlayerView 对 exoPlayerView 动态添加、移除。
     */
    override var attachedView: WrapperPlayerView? = null
    override val isPlaying: Boolean
        get() = playing

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(app)
        .setMediaSourceFactory(DefaultMediaSourceFactory(PreloadManager.getCacheFactory()))
        .build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
        }
    private val exoPlayerView: StyledPlayerView = LayoutInflater.from(app).inflate(
        R.layout.layout_exo_player_view, null
    ) as StyledPlayerView
    private val exoControllerView: StyledPlayerControlView = LayoutInflater.from(app).inflate(
        R.layout.layout_exo_player_controller_view, null
    ) as StyledPlayerControlView

    private var playingUrl: String? = null
    private var playing: Boolean = false

    init {
        exoPlayerView.player = exoPlayer
        exoControllerView.player = exoPlayer
    }

    override fun inActive() {
        Log.d(TAG, "inActive: $playingUrl, attachedView=$attachedView")
        if (playingUrl.isNullOrEmpty() || attachedView == null) return
        exoPlayer.playWhenReady = false
        playing = false
        exoPlayer.removeListener(this)
        exoControllerView.removeVisibilityListener(this)
        exoControllerView.hide()
        attachedView?.inActive()
    }

    override fun onActive() {
        Log.d(TAG, "onActive: $playingUrl, attachedView=$attachedView")
        if (playingUrl.isNullOrEmpty() || attachedView == null) return
        exoPlayer.playWhenReady = true
        exoPlayer.addListener(this)
        exoControllerView.addVisibilityListener(this)
        exoControllerView.show()
        attachedView?.onActive(exoPlayerView, exoControllerView)
        if (exoPlayer.playbackState == Player.STATE_READY) {
            onPlaybackStateChanged(Player.STATE_READY)
        } else if (exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekTo(0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun togglePlay(attachView: WrapperPlayerView, videoUrl: String) {
        attachedView?.setOnTouchListener(null)
        attachView.setOnTouchListener { _, _ ->
            exoControllerView.show()
            true
        }
        if (videoUrl == playingUrl) {
            if (playing) inActive()
            else onActive()
        } else {
            inActive()
            playingUrl = videoUrl
            this.attachedView = attachView
            exoPlayer.setMediaSource(createMediaSource(videoUrl))
            exoPlayer.prepare()
            onActive()
        }
    }

    fun createMediaSource(videoUrl: String): MediaSource {
        return ExoProviders.progressiveMediaSourceFactory.createMediaSource(MediaItem.fromUri(videoUrl.toUri()))
    }

    override fun stop(release: Boolean) {
        playing = false
        playingUrl = null
        exoPlayer.playWhenReady = false
        exoControllerView.hideImmediately()
        attachedView?.removeView(exoPlayerView)
        attachedView?.removeView(exoControllerView)
        attachedView = null
        if (release) exoPlayer.release()
    }

    override fun onVisibilityChange(visibility: Int) {
        attachedView?.onVisibilityChanged(visibility, exoPlayer.playbackState == Player.STATE_ENDED)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        playing = playbackState == Player.STATE_READY && exoPlayer.playWhenReady
        attachedView?.onPlayerStateChanged(playing, playbackState)
    }

    override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        exoPlayer.playWhenReady = true
    }

    companion object {
        private val sPageListPlayers = mutableMapOf<String, IListPlayer>()
        fun get(pageName: String): IListPlayer {
            var pageListPlayer = sPageListPlayers[pageName]
            if (pageListPlayer == null) {
                pageListPlayer = ListPlayer()
                sPageListPlayers[pageName] = pageListPlayer
            }
            return pageListPlayer
        }

        fun stop(pageName: String, release: Boolean = true) {
            sPageListPlayers.remove(pageName)?.stop(release)

        }
    }
}