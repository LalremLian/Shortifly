
package com.lazydeveloper.shortifly.player

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
) : ViewModel(), PlayerControls {
    //For the visibility of the player
    private val _playerVisibility = MutableLiveData<Boolean>()
    val playerVisibility: LiveData<Boolean> get() = _playerVisibility
    private val _playerState = MutableLiveData<Int>()
    val playerState: LiveData<Int> get() = _playerState
    private var playbackPosition: Long = 0
    private var playWhenReady: Boolean = false

    private val _isFullScreen = MutableLiveData<Boolean>()
    val isFullScreen: LiveData<Boolean> get() = _isFullScreen

    private var originalWidth = 0
    private var originalHeight = 0

    private val _watchTime = MutableStateFlow(0L)
    val watchTime: StateFlow<Long> = _watchTime
    private var playbackStartPosition: Long = 0
    private var playbackStartTime: Long = 0
    private var isPlaying = false

    init {
        _isFullScreen.value = false
        _playerVisibility.value = true
    }
    fun toggleFullScreen(activity: FragmentActivity) {
        val layoutParams = if (_isFullScreen.value == true) {
            // Revert to normal mode
            originalWidth to originalHeight
        } else {
            // Enter full-screen mode
            originalWidth = activity.window.decorView.width
            originalHeight = activity.window.decorView.height
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            0 to 0
        }
        _isFullScreen.value = !_isFullScreen.value!!
        // Reset orientation when exiting full-screen mode
        if (!_isFullScreen.value!!) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    private fun calculateFixedHeight(): Int {
        // Calculate and return the fixed height based on your requirements
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        return screenHeight / 2
    }

    suspend fun setPlayerVisibility() {
        delay(2500).apply {
            _playerVisibility.value = false
        }
    }

    fun togglePlayerVisibility() {
        _playerVisibility.value = !_playerVisibility.value!!
    }

    private val isPlayingStateFlow = MutableStateFlow(true)
//    val isPlayingStateFlow = _isPlayingStateFlow.asStateFlow()

    private val _isPlaybackStartedStateFlow = MutableStateFlow(false)
    val isPlaybackStartedStateFlow = _isPlaybackStartedStateFlow.asStateFlow()

    private val _isPlayerLoadingStateFlow = MutableStateFlow(true)
    val isPlayerLoadingStateFlow = _isPlayerLoadingStateFlow.asStateFlow()

    private val _playerOrientationStateFlow = MutableStateFlow(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    private val playerOrientationStateFlow = _playerOrientationStateFlow.asStateFlow()

    private val _duration = MutableStateFlow<Long>(0)
    val duration: StateFlow<Long> = _duration

    private val _currentPosition = MutableStateFlow<Long>(0)
    val currentPosition: StateFlow<Long> = _currentPosition
    
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
        initializePlayer()
    }

    private fun initializePlayer() {
        // Initialize your player here
        // Add a listener to observe player events
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                // Notify the Fragment/Activity about the player error
                _playerState.postValue(Player.STATE_READY)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // Notify the Fragment/Activity about the playback state change
                _playerState.postValue(Player.STATE_READY)
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                // Notify the Fragment/Activity about the playback state change
                _playerState.postValue(state)
                when (state) {
                    Player.STATE_IDLE -> {
                        // Video playback is idle
                        updateWatchTime()
                    }
                    Player.STATE_BUFFERING -> {
                        // Video is buffering
                    }
                    Player.STATE_READY -> {
                        // Video is ready to play
                        if (!isPlaying) {
                            playbackStartTime = System.currentTimeMillis()
                            isPlaying = true
                        }
                        updateWatchTime()
                    }
                    Player.STATE_ENDED -> {
                        // Video playback ended
                        isPlaying = false
                        updateWatchTime()
                    }
                }
            }
        })
    }
    private fun updateWatchTime() {
        val currentPosition = player.currentPosition
        val elapsedTime = if (isPlaying) {
            System.currentTimeMillis() - playbackStartTime
        } else {
            0
        }
        _watchTime.value = playbackStartPosition + currentPosition + elapsedTime
    }

    fun releasePlayer() {
        player.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
        }
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

}

@UnstableApi data class PlayerState(
    val isPlaying: Boolean = true,
    val isPlaybackStarted: Boolean = false,
    val isPlayerLoading: Boolean = true,
//    val currentVideoItem: VideoItem? = null,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
)
