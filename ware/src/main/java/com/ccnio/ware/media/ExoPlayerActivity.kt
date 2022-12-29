package com.ccnio.ware.media

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
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
        val playerView: SurfaceView = findViewById(R.id.surface_view)
        val videoUri = "https://v-cdn.zjol.com.cn/276982.mp4"
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)  // Build the media item.
//        setResolution()
        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSource.Factory()// Create a data source factory.
//        val cacheDataSourceFactory: DataSource.Factory =
//            CacheDataSource.Factory().setUpstreamDataSourceFactory(dataSourceFactory) //cache
        player = ExoPlayer.Builder(this).build()

        player.setVideoSurfaceView(playerView)
        player.setMediaItem(mediaItem)  // Set the media item to be played.
        player.prepare() // Prepare the player.
        player.play() // Start the playback.
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

    override fun onBackPressed() {
        player.pause()
    }
}
