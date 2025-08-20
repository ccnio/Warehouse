package com.ccino.demo.media.list

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ccino.demo.databinding.PlayerActivityDetailBinding
import com.ccino.demo.media.VideoInfo
import com.ccino.demo.util.DisplayUtil

class PlayerDetailActivity : AppCompatActivity() {
    private lateinit var binding: PlayerActivityDetailBinding

    private val player by lazy { (ListPlayer.get("PlayerListMain") as ListPlayer).getExoPlayer() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = PlayerActivityDetailBinding.inflate(layoutInflater)
        (binding.playerView.layoutParams as ViewGroup.MarginLayoutParams).height = DisplayUtil.screenHeight / 3
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        player.playWhenReady = true
        binding.playerView.player = player
    }

    override fun onPause() {
        super.onPause()
        Log.d("ccino", "onPause: ")
        player.pause()
    }

    override fun onStop() {
        super.onStop()
        Log.d("ccino", "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ccino", "onDestroy: ")
    }

    companion object {
        private const val KEY_INFO_DATA = "video_data"
        fun startActivity(context: Context, view: View, data: VideoInfo) {

            val intent = Intent(context, PlayerDetailActivity::class.java)
            intent.apply {
                putExtra(KEY_INFO_DATA, data)
            }
            val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity, view, "shared_image").toBundle()
            context.startActivity(intent, options)
        }
    }
}