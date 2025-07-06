package com.ccino.demo.media.cust

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.core.net.toUri
import com.ccino.demo.R
import com.ccino.demo.app
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache

class PageListPlayer : IListPlayer, Player.Listener, StyledPlayerControlView.VisibilityListener {
    override var attachedView: WrapperPlayerView? = null
    override val isPlaying: Boolean
        get() = playing

    private val exoPlayer: ExoPlayer
    private val exoPlayerView: StyledPlayerView
    private val exoControllerView: StyledPlayerControlView

    private var playingUrl: String? = null
    private var playing: Boolean = false

    init {
        exoPlayer = ExoPlayer.Builder(app).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
        }
        exoPlayerView = LayoutInflater.from(app).inflate(
            R.layout.layout_exo_player_view, null
        ) as StyledPlayerView
        exoControllerView = LayoutInflater.from(app).inflate(
            R.layout.layout_exo_player_controller_view, null
        ) as StyledPlayerControlView

        exoPlayerView.player = exoPlayer
        exoControllerView.player = exoPlayer
    }

    override fun inActive() {
        if (playingUrl.isNullOrEmpty() || attachedView == null) return
        exoPlayer.playWhenReady = false
        exoPlayer.removeListener(this)
        exoControllerView.removeVisibilityListener(this)
        exoControllerView.hide()
        attachedView?.inActive()
    }

    override fun onActive() {
        if (playingUrl.isNullOrEmpty() || attachedView == null) return
        exoPlayer.playWhenReady = true
        exoPlayer.addListener(this)
        exoControllerView.addVisibilityListener(this)
        exoControllerView.show()
        attachedView?.onActive(exoPlayerView, exoControllerView)
        if(exoPlayer.playbackState == Player.STATE_READY) {
            onPlaybackStateChanged(Player.STATE_READY)
        } else if( exoPlayer.playbackState == Player.STATE_ENDED) {
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
            exoPlayer.setMediaSource(createMediaSour(videoUrl))
            exoPlayer.prepare()
            onActive()
        }
    }

    fun createMediaSour(videoUrl: String): MediaSource {
        return progressiveDataSourceFactory.createMediaSource(MediaItem.fromUri(videoUrl.toUri()))
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
        private val cache = SimpleCache(app.cacheDir, LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100), StandaloneDatabaseProvider(app))
        private val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(cache).setFragmentSize(Long.MAX_VALUE))
            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE)
        private val progressiveDataSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)

        private val sPageListPlayers = mutableMapOf<String, IListPlayer>()
        fun get(pageName: String): IListPlayer {
            var pageListPlayer = sPageListPlayers[pageName]
            if (pageListPlayer == null) {
                pageListPlayer = PageListPlayer()
                sPageListPlayers[pageName] = pageListPlayer
            }
            return pageListPlayer
        }

        fun stop(pageName: String, release: Boolean = true) {
            sPageListPlayers.remove(pageName)?.stop(release)

        }
    }
}