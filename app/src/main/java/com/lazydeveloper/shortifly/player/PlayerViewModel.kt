package com.lazydeveloper.shortifly.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.TrackSelectionDialogBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: ExoPlayer,
    val trackSelector: DefaultTrackSelector
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
    var videoDuration = 0L

    init {
        _isFullScreen.value = false
        _playerVisibility.value = true
    }
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            updateWatchTime()
            handler.postDelayed(this, 1000)
        }
    }
    fun toggleFullScreen(activity: FragmentActivity) {
        if (_isFullScreen.value == true) {
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
        videoDuration = player.duration
        initializePlayer()
    }

    private val _currentPosition2 = MutableLiveData<Long>()
    val currentPosition2: LiveData<Long> get() = _currentPosition2

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        // Initialize your player here
        // Add a listener to observe player events
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                // Notify the Fragment/Activity about the player error
                _playerState.postValue(Player.STATE_READY)
            }

            override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
                super.onTrackSelectionParametersChanged(parameters)
                Log.e("VideoScreen", "onTrackSelectionParametersChanged")
                val currentProgressTime = player.currentPosition
                player.stop()
                player.seekTo(currentProgressTime)
                player.apply {
                    prepare()
                    playWhenReady = true
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    handler.post(runnable)
                } else {
                    handler.removeCallbacks(runnable)
                }
                // Notify the Fragment/Activity about the playback state change
                _playerState.postValue(Player.STATE_READY)
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                // Notify the Fragment/Activity about the playback state change
                _playerState.postValue(state)
                when (state) {
                    Player.STATE_IDLE -> {
                        updateWatchTime()
                    }
                    Player.STATE_BUFFERING -> {
                        updateWatchTime()
                    }
                    Player.STATE_READY -> {
                        if (!isPlaying) {
                            playbackStartTime = System.currentTimeMillis()
                            isPlaying = true
                        }
                        updateWatchTime()
                    }
                    Player.STATE_ENDED -> {
                        isPlaying = false
                        updateWatchTime()
                    }
                }
            }
        })
    }

    fun trackSelection(context: Context) {
        val trackSelector = this.trackSelector
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        if (mappedTrackInfo != null) {
            /*            The index usually follows this order:
                            0: video
                            1: audio
                            2: closed captions (text)*/
            val rendererIndex = 2
            val rendererType = mappedTrackInfo.getRendererType(rendererIndex)
            val allowAdaptiveSelections =
                rendererType == C.TRACK_TYPE_VIDEO
                        || (rendererType == C.TRACK_TYPE_AUDIO
                        && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS)

            val builder =
                TrackSelectionDialogBuilder(context, "Select Resolution", player, rendererIndex)
            builder.setShowDisableOption(false)
            builder.setAllowAdaptiveSelections(allowAdaptiveSelections)
            builder.setOverrides(player.trackSelectionParameters.overrides)
            builder.build().show()

        }
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    }

    private fun updateWatchTime() {
        _currentPosition2.postValue(player.currentPosition)
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

    override fun setLoadingState(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isPlaybackStarted(value: Boolean) {
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
        TODO("Not yet implemented")
    }


    override fun resizeVideoFrame() {
        TODO("Not yet implemented")
    }

//    override fun playNewVideo(item: VideoItem) {
//        player.clearMediaItems()
//        updateCurrentVideoItem(item)
//    }

}

