package com.lazydeveloper.shortifly.player

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.util.MimeTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
) : ViewModel(), PlayerControls {
    
    private val isPlayingStateFlow = MutableStateFlow(true)
//    val isPlayingStateFlow = _isPlayingStateFlow.asStateFlow()
    
    private val _isPlaybackStartedStateFlow = MutableStateFlow(false)
    val isPlaybackStartedStateFlow = _isPlaybackStartedStateFlow.asStateFlow()
    
    private val _isPlayerLoadingStateFlow = MutableStateFlow(true)
    val isPlayerLoadingStateFlow = _isPlayerLoadingStateFlow.asStateFlow()
    
//    private val _currentVideoItemStateFlow = MutableStateFlow<VideoItem?>(null)
//    val currentVideoItemStateFlow = _currentVideoItemStateFlow.asStateFlow()
    
    private val _resizeModeStateFlow = MutableStateFlow(AspectRatioFrameLayout.RESIZE_MODE_FIT)
    val resizeModeStateFlow = _resizeModeStateFlow.asStateFlow()
    
    private val _playerOrientationStateFlow = MutableStateFlow(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val playerOrientationStateFlow = _playerOrientationStateFlow.asStateFlow()
    
//    init {
//        player.apply { prepare() }
//    }
    
    override fun onCleared() {
        super.onCleared()
        player.release()
    }
    
//    private fun updateCurrentVideoItem(videoItem: VideoItem) {
//        _currentVideoItemStateFlow.value = videoItem
//        setMediaItem(videoItem.contentUri)
//    }
    
    fun setMediaItem(uri: Uri) {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()
        
        player.apply {
            addMediaItem(mediaItem)
            playWhenReady = true
            if (isPlaying) {
                isPlayingStateFlow.value = true
            }
        }
        player.apply { prepare() }
    }
    
    override fun playPause() {
        if (player.isPlaying) {
            player.pause().also {
                isPlayingStateFlow.value = false
            }
        } else {
            player.play().also {
                isPlayingStateFlow.value = true
            }
        }
    }
    override fun forward(durationMs: Long) {
        player.apply {
            this.seekTo(this.currentPosition + durationMs)
        }
    }
    override fun rewind(durationMs: Long) {
        player.apply {
            this.seekTo(this.currentPosition - durationMs)
        }
    }
    
    override fun pip() {
        TODO("Not yet implemented")
    }

    override fun handlePlaybackOnLifecycle(value: Boolean) {
        if (player.isPlaying && value) {
            player.pause().also {
                isPlayingStateFlow.value = false
            }
        } else if (!player.isPlaying && !value) {
            player.play().also {
                isPlayingStateFlow.value = true
            }
        }
    }
    
    override fun rotateScreen() {
        val orientation = if (playerOrientationStateFlow.value == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        _playerOrientationStateFlow.value = orientation
    }

    override fun resizeVideoFrame() {
        TODO("Not yet implemented")
    }

//    override fun playNewVideo(item: VideoItem) {
//        player.clearMediaItems()
//        updateCurrentVideoItem(item)
//    }
    
    override fun setLoadingState(value: Boolean) {
        _isPlayerLoadingStateFlow.value = value
    }

    override fun isPlaybackStarted(value: Boolean) {
        _isPlaybackStartedStateFlow.value = value
    }
    
    companion object {
        const val TAG = "PlayerViewModel"
    }
}

data class PlayerState(
    val isPlaying: Boolean = true,
    val isPlaybackStarted: Boolean = false,
    val isPlayerLoading: Boolean = true,
//    val currentVideoItem: VideoItem? = null,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
)