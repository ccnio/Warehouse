package com.ccnio.ware.media

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


private const val TAG = "ExoPlayerLog"

class ExoPlayerActivity : AppCompatActivity(), View.OnClickListener {
    private val simpleCache by lazy {
        SimpleCache(
            File(getExternalFilesDir(null)!!.absolutePath),
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 600),
            ExoDatabaseProvider(this),
            null,
            false,
            false
        )
    } //全局配置: 应该使用单例
    private val trackSelector by lazy { DefaultTrackSelector(this) }

    private lateinit var player: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exoplayer)
        val videoUri = "https://v-cdn.zjol.com.cn/276982.mp4"
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)  // Build the media item.
        player = ExoPlayer.Builder(this).build()
        player.setMediaItem(mediaItem)  // Set the media item to be played.

        /*   //1. surface view
           val playerView: SurfaceView = findViewById(R.id.surface_view)
           player.setVideoSurfaceView(playerView)*/

        //2. player view
        val playerView: PlayerView = findViewById(R.id.player_view)
        playerView.player = player

        //        setResolution()
        //val dataSourceFactory: DataSource.Factory =
        //   DefaultHttpDataSource.Factory()// Create a data source factory.
        //        val cacheDataSourceFactory: DataSource.Factory =
        //            CacheDataSource.Factory().setUpstreamDataSourceFactory(dataSourceFactory) //cache

        player.addAnalyticsListener(DetailListener())
        player.addListener(PlayerListener())
        player.prepare() // Prepare the player.
        player.play() // Start the playback.
    }

    private inner class DetailListener : AnalyticsListener {

    }


    private inner class PlayerListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> {
                    "ExoPlayer.STATE_BUFFERING -"
                }
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }

        override fun onEvents(player: Player, events: Player.Events) {
            Log.d(TAG, "onEvents: $events")
        }
    }


    private val BUFFER_TRACK = 1
    private val BUFFER_SEEK = 2
    private val BUFFER_ELSE = 3
    private var bufferReason = BUFFER_ELSE

    private var resolutionFlag = true
    private fun setResolution() {
        val width = if (resolutionFlag) 1920 else 1280 //onVideoSizeChanged: width = 1920; h = 1080
        val height = if (resolutionFlag) 1080 else 720 //onVideoSizeChanged: width = 1280; h = 720
        trackSelector.parameters =
            trackSelector.parameters.buildUpon().setMaxVideoSize(width, height)
                .setMinVideoSize(width, height)
                .build()//.setMaxVideoBitrate(14442347) //14442347 16361812 42324493
        resolutionFlag = !resolutionFlag
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleCache.release()
        player.release()
    }

    override fun onClick(v: View) {
        when (v.id) {
        }
    }
}
