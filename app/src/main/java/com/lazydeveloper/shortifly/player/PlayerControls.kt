package com.lazydeveloper.shortifly.player

interface PlayerControls {
    fun playPause()
    fun forward(durationMs :Long)
    fun rewind(durationMs :Long)
    fun handlePlaybackOnLifecycle(value: Boolean)
    fun rotateScreen()
    fun resizeVideoFrame()
    fun pip()
//    fun playNewVideo(item: VideoItem)
    fun setLoadingState(value: Boolean)
    fun isPlaybackStarted(value: Boolean)
}

