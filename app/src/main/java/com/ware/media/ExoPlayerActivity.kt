package com.ware.media

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.video.VideoSize
import com.ware.R
import com.ware.compatibility.notch.NotchScreenManager
import kotlinx.android.synthetic.main.activity_exoplayer.*
import java.io.File


private const val TAG = "ExoPlayerLog"

class ExoPlayerActivity : AppCompatActivity(), View.OnClickListener {
    private val simpleCache by lazy {
        SimpleCache(File(getExternalFilesDir(null)!!.absolutePath), LeastRecentlyUsedCacheEvictor(1024 * 1024 * 600),
            ExoDatabaseProvider(this), null, false, false)
    } //全局配置: 应该使用单例
    private val trackSelector by lazy { DefaultTrackSelector(this) }

    private lateinit var player: SimpleExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        NotchScreenManager.getInstance().setDisplayInNotch(this)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)//去掉Activity上面的状态栏
        setContentView(R.layout.activity_exoplayer)
        resolutionView.setOnClickListener(this)

//        val videoUri = "https://cdn.cnbj1.fds.api.mi-img.com/plato-xiaoai/f_1592818524702_02eb7fa3b3271ccc0e36a6c23f30e8eb.m3u8?GalaxyAccessKeyId=AKVGLQWBOVIRQ3XLEW&Expires=9223372036854775807&Signature=patbp2o9wJZHBbFkbNE+dywKCWQ="
        val videoUri = "https://v-cdn.zjol.com.cn/276982.mp4"
        //        val videoUri = "https://staging-cnbj2-fds.api.xiaomi.net/hlth-operate/video/1621931691018_35f5924267e08dd7efc7f3df523f37e7.m3u8?GalaxyAccessKeyId=AKQAB4U5JA25UMUCY3&Expires=9223372036854775807&Signature=jBwmp0SHeKw2ObIF2P+wx/uruxE="
//        val videoUri = "https://staging-cnbj2-fds.api.xiaomi.net/hlth-operate/course/video/2021/07/15/185857_40e20381784a88350c95e495d6358688.m3u8?GalaxyAccessKeyId=AKQAB4U5JA25UMUCY3&Expires=9223372036854775807&Signature=w2fzkg7LVyGUWgNL/0vjMBT3mfU="
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)  // Build the media item.
        setResolution()
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()// Create a data source factory.
        val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory().setCache(simpleCache).setUpstreamDataSourceFactory(dataSourceFactory) //cache
        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)).build()

        tvView.setOnClickListener { player.stop() }
//        player.setVideoSurfaceView(playerView )
        playerView.player = player
        player.setMediaItem(mediaItem)  // Set the media item to be played.
        player.prepare() // Prepare the player.
        player.play() // Start the playback.
        player.addListener(PlayerListener()) //Player.Listener
        player.addAnalyticsListener(DetailListener())//详细事件，这些事件可能用于分析和报告的目的
    }

    private val BUFFER_TRACK = 1
    private val BUFFER_SEEK = 2
    private val BUFFER_ELSE = 3
    private var bufferReason = BUFFER_ELSE

    private inner class PlayerListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> {
                    Log.d(TAG, "onPlaybackStateChanged: bufferRea = $bufferReason")
                    bufferReason = BUFFER_ELSE
                    "ExoPlayer.STATE_BUFFERING -"
                }
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
            Log.d(TAG, "onTracksChanged: $trackGroups; $trackSelections")
            bufferReason = BUFFER_TRACK
        }

        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            Log.d(TAG, "onPositionDiscontinuity: $newPosition; $reason")
            bufferReason = BUFFER_SEEK
        }


        //
//
//        override fun onVideoSizeChanged(videoSize: VideoSize) {
//            Log.d(TAG, "onVideoSizeChanged: width = ${videoSize.width}; h = ${videoSize.height}")
//            courseLayout.setRation(videoSize.width, videoSize.height)
//        }
//
//        override fun onIsPlayingChanged(isPlaying: Boolean) {
//            Log.d(TAG, "onIsPlayingChanged: isPlaying = $isPlaying")
//        }
//
        override fun onPlayerError(error: ExoPlaybackException) {
            Log.d(TAG, "onPlayerError: $error")
        }
    }

    private class DetailListener : AnalyticsListener {
        override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
            Log.d(TAG, "onPlaybackStateChanged: $state; isBuffer = ${state == Player.STATE_BUFFERING}")
        }

        override fun onPlayWhenReadyChanged(eventTime: AnalyticsListener.EventTime, playWhenReady: Boolean, reason: Int) {
            Log.d(TAG, "onPlayWhenReadyChanged: $playWhenReady; reason = $reason")
        }

        override fun onPlaybackSuppressionReasonChanged(eventTime: AnalyticsListener.EventTime, playbackSuppressionReason: Int) {
            Log.d(TAG, "onPlaybackSuppressionReasonChanged: ")
        }

        override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
            Log.d(TAG, "onIsPlayingChanged: $isPlaying")
        }

        override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime, oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            Log.d(TAG, "onPositionDiscontinuity: $reason")
        }

        override fun onPlaybackParametersChanged(eventTime: AnalyticsListener.EventTime, playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(eventTime, playbackParameters)
            Log.d(TAG, "onPlaybackParametersChanged: ")
        }

//            override fun onIsLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
//                super.onIsLoadingChanged(eventTime, isLoading)
//                Log.d(TAG, "onIsLoadingChanged: isLoading = $isLoading")
//            }

        override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: ExoPlaybackException) {
            Log.d(TAG, "onPlayerError: ${error.type}; ${error.sourceException}")
        }

        override fun onTracksChanged(eventTime: AnalyticsListener.EventTime, trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
            Log.d(TAG, "onTracksChanged: ")
        }

        override fun onStaticMetadataChanged(eventTime: AnalyticsListener.EventTime, metadataList: MutableList<Metadata>) {
            Log.d(TAG, "onStaticMetadataChanged: ")
        }

        override fun onMediaMetadataChanged(eventTime: AnalyticsListener.EventTime, mediaMetadata: MediaMetadata) {
            Log.d(TAG, "onMediaMetadataChanged: ")
        }
//
//            override fun onLoadStarted(eventTime: AnalyticsListener.EventTime, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData) {
//                Log.d(TAG, "onLoadStarted: ")
//            }
//
//            override fun onLoadCompleted(eventTime: AnalyticsListener.EventTime, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData) {
//                Log.d(TAG, "onLoadCompleted: ")
//            }
//
//            override fun onLoadCanceled(eventTime: AnalyticsListener.EventTime, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData) {
//                Log.d(TAG, "onLoadCanceled: ")
//            }
//
//            override fun onLoadError(eventTime: AnalyticsListener.EventTime, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData, error: IOException, wasCanceled: Boolean) {
//                Log.d(TAG, "onLoadError: error = ${error}; wasCanceled = $wasCanceled")
//            }

        override fun onDownstreamFormatChanged(eventTime: AnalyticsListener.EventTime, mediaLoadData: MediaLoadData) {
            Log.d(TAG, "onDownstreamFormatChanged: ")
        }

        override fun onUpstreamDiscarded(eventTime: AnalyticsListener.EventTime, mediaLoadData: MediaLoadData) {
            Log.d(TAG, "onUpstreamDiscarded: ")
        }

        override fun onBandwidthEstimate(eventTime: AnalyticsListener.EventTime, totalLoadTimeMs: Int, totalBytesLoaded: Long, bitrateEstimate: Long) {
            Log.d(TAG, "onBandwidthEstimate: ")
        }

        override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
            Log.d(TAG, "onMetadata: ")
        }

        override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime, videoSize: VideoSize) {
            Log.d(TAG, "onVideoSizeChanged: $videoSize")
        }

        override fun onEvents(player: Player, events: AnalyticsListener.Events) {
            super.onEvents(player, events)
        }

        override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {
            Log.d(TAG, "onSeekStarted: ")
        }

        override fun onSeekProcessed(eventTime: AnalyticsListener.EventTime) {
            Log.d(TAG, "onSeekProcessed: ")
        }
    }

    private var resolutionFlag = true
    private fun setResolution() {
        val width = if (resolutionFlag) 1920 else 1280 //onVideoSizeChanged: width = 1920; h = 1080
        val height = if (resolutionFlag) 1080 else 720 //onVideoSizeChanged: width = 1280; h = 720
        trackSelector.parameters =
            trackSelector.parameters.buildUpon().setMaxVideoSize(width, height).setMinVideoSize(width, height).build()//.setMaxVideoBitrate(14442347) //14442347 16361812 42324493
        resolutionFlag = !resolutionFlag
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleCache.release()
        player.release()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.resolutionView -> setResolution()
        }
    }

    override fun onBackPressed() {
        player.pause()
    }
}
