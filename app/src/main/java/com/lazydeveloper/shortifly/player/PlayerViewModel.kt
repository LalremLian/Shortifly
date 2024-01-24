package com.lazydeveloper.shortifly.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
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
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.lazydeveloper.shortifly.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: ExoPlayer,
    private val trackSelector: DefaultTrackSelector
) : ViewModel(), PlayerControls {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    //For the visibility of the player
    private val _playerVisibility = MutableLiveData<Boolean>()
    val playerVisibility: LiveData<Boolean> get() = _playerVisibility

    private val _playerState = MutableLiveData<Int>()
    val playerState: LiveData<Int> get() = _playerState

    private var playbackPosition: Long = 0
    private var playWhenReady: Boolean = false

    private val _isFullScreen = MutableLiveData<Boolean>()
    val isFullScreen: LiveData<Boolean> get() = _isFullScreen

    private val _selectedFormat = MutableLiveData<String>()
    val selectedFormat: LiveData<String> = _selectedFormat

    private var originalWidth = 0
    private var originalHeight = 0

    private var isPlaying = false
    var videoDuration = 0L

    init {
        _isFullScreen.value = false
        _playerVisibility.value = true
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

    private val _currentPosition = MutableLiveData<Long>()
    val currentPosition: LiveData<Long> get() = _currentPosition

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        // Initialize your player here
        // Add a listener to observe player events
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                _playerState.postValue(Player.EVENT_PLAYER_ERROR)
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
                    coroutineScope.launch {
                        while (isActive) {
                            updateWatchTime()
                            delay(1000) // Update every second
                        }
                    }
                }
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
                        isPlaying = false
                        updateWatchTime()
                    }

                    Player.STATE_READY -> {
                        isPlaying = true
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
            val rendererIndex = 2
            val rendererType = mappedTrackInfo.getRendererType(rendererIndex)
            val allowAdaptiveSelections =
                ((rendererType == C.TRACK_TYPE_VIDEO)
                        || ((rendererType == C.TRACK_TYPE_AUDIO)
                        && (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS)))

            val builder = TrackSelectionDialogBuilder(
                context,
                "Select Resolution",
                player,
                rendererIndex
            )
            builder.setTheme(R.style.ResolutionDialogTheme)
            builder.setShowDisableOption(false)
            builder.setAllowAdaptiveSelections(allowAdaptiveSelections)
            builder.setOverrides(player.trackSelectionParameters.overrides)
            builder.setTrackNameProvider { format ->
                val formatName = "${format.height}p"
                _selectedFormat.postValue(formatName) // Update the selected format
                formatName
            }
            builder.build().show()
        }
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    }

    private fun updateWatchTime() {
        _currentPosition.postValue(player.currentPosition)
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

//    override fun playNewVideo(item: VideoItem) {
//        player.clearMediaItems()
//        updateCurrentVideoItem(item)
//    }

}

