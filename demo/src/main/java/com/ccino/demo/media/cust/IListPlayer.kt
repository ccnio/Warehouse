package com.ccino.demo.media.cust

interface IListPlayer {
    /**
     * 当前播放器的exoPlayer(textureView)
     */
    val attachedView: WrapperPlayerView?
    val isPlaying: Boolean

    /**
     * 页面不可见时，暂停播放
     */
    fun inActive()

    /**
     * 恢复播放
     */
    fun onActive()
    fun togglePlay(attachView: WrapperPlayerView, videoUrl: String)
    fun stop(release: Boolean)

}