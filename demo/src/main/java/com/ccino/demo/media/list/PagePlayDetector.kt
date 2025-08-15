package com.ccino.demo.media.list

import android.graphics.Rect
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class PagePlayDetector(
    private val pageName: String,
    private val lifecycleOwner: LifecycleOwner,
    private val listView: RecyclerView
) {
    private val pageListPlayer = PageListPlayer.get(pageName)
    private val detectorListener = mutableListOf<IPlayDetector>()
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay()
            }
        }


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx == 0 && dy == 0) {
                postAutoPlay()
            } else {
                if (pageListPlayer.isPlaying && !isTargetInBounds(pageListPlayer.attachedView)) {
                    pageListPlayer.inActive()
                }
            }
        }


    }

    init {
        listView.addOnScrollListener(scrollListener)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> pageListPlayer.onActive()
                    Lifecycle.Event.ON_PAUSE -> pageListPlayer.inActive()
                    Lifecycle.Event.ON_DESTROY -> {
                        pageListPlayer.stop(false)
                        detectorListener.clear()
                        listView.removeOnScrollListener(scrollListener)
                        listView.removeCallbacks(postRunnable)
                    }

                    else -> {}
                }
            }
        })
    }

    private fun isTargetInBounds(attachView: ViewGroup?): Boolean {
        attachView ?: return false
        if (!attachView.isVisible || !attachView.isAttachedToWindow) return false
        val visibleRect = Rect()
        val isVisible = attachView.getGlobalVisibleRect(visibleRect)
        if (!isVisible) return false
        val viewArea = attachView.width * attachView.height
        if (viewArea <= 0) return false
        val visibleArea = visibleRect.width() * visibleRect.height()
        val ratio = visibleArea.toFloat() / viewArea
        return ratio >= 0.5f
    }

    private fun autoPlay() {
        if (detectorListener.isEmpty() || listView.isEmpty()) {
            return
        }

        if (pageListPlayer.isPlaying && isTargetInBounds(pageListPlayer.attachedView)) return

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
        pageListPlayer.togglePlay(attachView, videoUrl)
    }

    val postRunnable = Runnable { autoPlay() }
    private fun postAutoPlay() {
        listView.post(postRunnable)
    }

    fun requestAutoPlay() {
        postAutoPlay()
    }

    fun addDetector(listener: IPlayDetector) {
        detectorListener.add(listener)
    }

    fun removeDetector(detector: IPlayDetector) {
        detectorListener.remove(detector)
    }

    interface IPlayDetector {
        fun getAttachView(): WrapperPlayerView
        fun getVideoUrl(): String
    }
}