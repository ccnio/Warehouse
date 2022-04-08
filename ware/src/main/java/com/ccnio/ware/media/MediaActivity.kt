package com.ccnio.ware.media

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.RawResourceDataSource


class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        findViewById<View>(R.id.mediaPlayer).setOnClickListener { sysApi() }
    }

    private fun sysApi() {
/*
 各种异常，无法播放
 val mediaPlayer = MediaPlayer() //放在raw文件下总是报错
        val fd: AssetFileDescriptor = assets.openFd("cheer_achieved_reset.mp3")
        mediaPlayer.reset()
        mediaPlayer.setDataSource(fd.fileDescriptor) //经过笔者的测试，发现这个方法有时候不能播放成功，尽量使用该方法的另一个重载方法 setDataSource(FileDescptor fd,long offset,long length)
//        mediaPlayer.setDataSource(file.fileDescriptor, file.startOffset, file.length)
//        mediaPlayer.setAudioStreamType(Stream)
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
        mediaPlayer.prepareAsync()*/

        val player = ExoPlayer.Builder(this).build()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ONE
        val uri: Uri = RawResourceDataSource.buildRawResourceUri(R.raw.cheer_achieved_reset)

        val item = MediaItem.fromUri(uri)
        player.setMediaItem(item)  // Set the media item to be played.
        player.prepare()

    }
}