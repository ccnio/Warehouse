package com.ccino.demo.media

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ccino.demo.R
import com.ccino.demo.databinding.ActivityPlayerSwitchBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

private const val TAG = "PlayerSwitchActivity"

class PlayerSwitchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerSwitchBinding
    private val player by lazy { ExoPlayer.Builder(this).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerSwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initPlayer()

    }

    private fun initPlayer() {
        binding.playerView.player = player

        // 设置视频URL并播放
        val videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}