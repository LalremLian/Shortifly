package com.lazydeveloper.shortifly.interfaces

import com.google.android.exoplayer2.PlaybackException

interface ExoPlayerCallback {
    fun onPlayerError(error: PlaybackException)
    fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int)
}