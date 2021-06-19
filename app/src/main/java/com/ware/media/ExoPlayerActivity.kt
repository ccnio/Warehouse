package com.ware.media

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ware.R
import kotlinx.android.synthetic.main.activity_exoplayer.*
import java.io.File


private const val TAG = "ExoPlayerActivity"

class ExoPlayerActivity : AppCompatActivity() {
    private val simpleCache by lazy {
        SimpleCache(File(getExternalFilesDir(null)!!.absolutePath), LeastRecentlyUsedCacheEvictor(1024 * 1024 * 600),
                ExoDatabaseProvider(this), null, false, false)
    } //全局配置: 应该使用单例

    //    object VideoCache {
//        private var sDownloadCache: SimpleCache? = null
//        fun getInstance(context: Context): SimpleCache? {
//            if (sDownloadCache == null) sDownloadCache = SimpleCache(
//                File(context.getCacheDir(), "exoCache"),
//                NoOpCacheEvictor(),
//                ExoDatabaseProvider(context)
//            )
//            return sDownloadCache
//        }
//    }
//
    private lateinit var player: SimpleExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)//去掉Activity上面的状态栏
        setContentView(R.layout.activity_exoplayer)


        val videoUri = "https://staging-cnbj2-fds.api.xiaomi.net/hlth-operate/video/1621931691018_35f5924267e08dd7efc7f3df523f37e7.m3u8?GalaxyAccessKeyId=AKQAB4U5JA25UMUCY3&Expires=9223372036854775807&Signature=jBwmp0SHeKw2ObIF2P+wx/uruxE="
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)  // Build the media item.

        val trackSelector = DefaultTrackSelector(this)//14442347 16361812 42324493
//        val parameters = trackSelector.parameters.buildUpon().setMaxVideoBitrate(16361812).setMaxVideoSize(1920, 1080)
//                .setMinVideoSize(1900, 1000).build()
        val param = trackSelector.parameters.buildUpon()//.setMaxVideoBitrate(14442347) //14442347 16361812 42324493
                .setMaxVideoSize(1280, 720).setMinVideoSize(720, 1280).build()
        trackSelector.parameters = param


        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        //cache
        val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory().setCache(simpleCache)
                .setUpstreamDataSourceFactory(dataSourceFactory)


        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector)
                .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)).build()
//        player.setVideoSurfaceView(playerView)
        playerView.player = player
        player.setMediaItem(mediaItem)  // Set the media item to be played.
        player.prepare() // Prepare the player.
        player.play() // Start the playback.


        lowView.setOnClickListener {
            val param = trackSelector.parameters.buildUpon()//.setMaxVideoBitrate(14442347) //14442347 16361812 42324493
                    .setMaxVideoSize(1280, 720).build()
            trackSelector.parameters = param

        }
        highView.setOnClickListener {
            val param = trackSelector.parameters.buildUpon()//.setMaxVideoBitrate(16361812) //14442347 16361812 42324493
                    .setMaxVideoSize(1920, 1080).build()
            trackSelector.parameters = param
        }
//        highView.postDelayed({
//            MDialog.Builder().setLayoutRes(R.layout.course_layout_alert).setGravity(Gravity.CENTER).create().show(supportFragmentManager)
//        }, 5000)
        Log.d(TAG, "onCreate: duration = ${player.contentDuration}")
        seekNextView.setOnClickListener { player.seekTo(player.currentPosition + 100000) }
        player.addListener(PlaybackStateListener())
    }

    private inner class PlaybackStateListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            Log.d(TAG, "onEvents: event = ${events.toString()}")
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            Log.d(TAG, "onPlayWhenReadyChanged: $playWhenReady")
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> {
                    Log.d(TAG, "onPlaybackStateChanged: ${player.contentDuration}")
                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        simpleCache.release()
    }

}
