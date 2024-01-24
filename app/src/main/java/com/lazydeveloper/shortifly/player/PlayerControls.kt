package com.lazydeveloper.shortifly.player

interface PlayerControls {
    fun playPause()
    fun forward(durationMs :Long)
    fun rewind(durationMs :Long)
    fun handlePlaybackOnLifecycle(value: Boolean)
}

