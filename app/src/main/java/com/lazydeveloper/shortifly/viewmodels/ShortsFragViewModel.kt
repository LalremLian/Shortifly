package com.lazydeveloper.shortifly.viewmodels

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.lazydeveloper.shortifly.ExoPlayerItem

class ShortsFragViewModel: ViewModel() {
    val exoPlayerItems = ArrayList<ExoPlayerItem>()

    fun addExoPlayerItem(exoPlayerItem: ExoPlayerItem) {
        exoPlayerItems.add(exoPlayerItem)
    }

    fun getExoPlayerItem(position: Int): ExoPlayer? {
        return exoPlayerItems.firstOrNull { it.position == position }?.exoPlayer
    }

    fun pausePlayer(position: Int) {
        getExoPlayerItem(position)?.apply {
            pause()
            playWhenReady = false
        }
    }

    fun playPlayer(position: Int) {
        getExoPlayerItem(position)?.apply {
            playWhenReady = true
            play()
        }
    }

    fun stopAndClearPlayers() {
        for (item in exoPlayerItems) {
            val player = item.exoPlayer
            player.stop()
            player.clearMediaItems()
        }
    }
}