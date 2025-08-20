package com.ccino.demo.media.list

import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

private const val TAG = "PlayerListDetector"
class ListDetector(pageName: String, lifecycleOwner: LifecycleOwner, private val listView: RecyclerView) {
    private val player = ListPlayer.get(pageName)
    private val detectorListener = mutableListOf<IPlayDetector>()
    private var preState = -22
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                Log.d(TAG, "onScrollStateChanged: scroll idle, ${recyclerView.y}")
                autoPlay()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx == 0 && dy == 0) {
                Log.d(TAG, "onScrolled: dx/dy=0")
                postAutoPlay()
            } else {
                if (player.isPlaying && !isTargetInBounds(player.attachedView)) {
                    Log.d(TAG, "onScrolled: enter inActive")
                    player.inActive()
                }
            }
        }
    }

    init {
        listView.addOnScrollListener(scrollListener)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> player.onActive()
                    Lifecycle.Event.ON_PAUSE -> player.inActive()
                    Lifecycle.Event.ON_DESTROY -> {
                        player.stop(false)
                        detectorListener.clear()
                        listView.removeOnScrollListener(scrollListener)
                        listView.removeCallbacks(postRunnable)
                    }

                    else -> {}
                }
            }
        })
    }

    private val visibleRect = Rect()
    private fun isTargetInBounds(attachView: ViewGroup?): Boolean {
        attachView ?: return false
        if (!attachView.isVisible || !attachView.isAttachedToWindow) return false
        val isVisible = attachView.getGlobalVisibleRect(visibleRect)
        if (!isVisible) return false
        val viewArea = attachView.width * attachView.height
        if (viewArea <= 0) return false
        val visibleArea = visibleRect.width() * visibleRect.height()
        val ratio = visibleArea.toFloat() / viewArea
        return ratio >= 0.5f
    }

    private fun autoPlay() {
        if (detectorListener.isEmpty() || listView.isEmpty()) return
        if (player.isPlaying && isTargetInBounds(player.attachedView)) return

        var attachViewListener: IPlayDetector? = null
        for (detector in detectorListener) {
            val attachView = detector.getAttachView()
            if (isTargetInBounds(attachView)) {
                attachViewListener = detector
                break
            }
        }
        attachViewListener?.run { togglePlay(getAttachView(), getVideoUrl()) }
    }

    fun togglePlay(attachView: WrapperPlayerView, videoUrl: String) {
        player.togglePlay(attachView, videoUrl)
    }

    val postRunnable = Runnable { autoPlay() }
    private fun postAutoPlay() {
        listView.post(postRunnable)
    }

    fun addDetector(listener: IPlayDetector) {
        detectorListener.add(listener)
    }

    fun removeDetector(detector: IPlayDetector) {
        detectorListener.remove(detector)
    }

    fun detachPlayer(playerView: WrapperPlayerView) {
        player.detachPlayer(playerView)
    }

    interface IPlayDetector {
        fun getAttachView(): WrapperPlayerView
        fun getVideoUrl(): String
    }
}