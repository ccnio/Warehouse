package com.ccnio.ware.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityExoplayerBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.google.android.exoplayer2.*


private const val TAG = "ExoPlayerLog"

class ExoPlayerActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityExoplayerBinding::bind)
    private val player by lazy { ExoPlayer.Builder(this).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exoplayer)
//        val videoUri = "https://v-cdn.zjol.com.cn/276982.mp4"
        val videoUri = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear3/prog_index.m3u8"
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)  // Build the media item.

        player.setMediaItem(mediaItem)  // Set the media item to be played.

        /*   //1. surface view
           val playerView: SurfaceView = findViewById(R.id.surface_view)
           player.setVideoSurfaceView(playerView)*/

        //2. player view
        bind.playerView.player = player

        player.prepare() // Prepare the player.
        player.play() // Start the playback.
    }
}
