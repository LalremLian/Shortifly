package com.lazydeveloper.shortifly.utils.extensions

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.lazydeveloper.shortifly.interfaces.ExoPlayerCallback

fun Context.initializeExoPlayer(): ExoPlayer {
    return ExoPlayer.Builder(this).build()
}

fun ExoPlayer.setupPlayer(context: Context, view: StyledPlayerView, url: String, callback: ExoPlayerCallback) {
    addListener(object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            callback.onPlayerError(error)
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            callback.onPlayerStateChanged(playWhenReady, playbackState)
        }
    })

    view.player = this

    seekTo(0)
    repeatMode = Player.REPEAT_MODE_ONE

    val dataSourceFactory = DefaultDataSource.Factory(context)

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

    setMediaSource(mediaSource)
    prepare()
}

fun ExoPlayer.initializeExoPlayer(context: Context) {
    repeatMode = Player.REPEAT_MODE_ONE
}

fun ExoPlayer.createMediaSource(videoUrl: String, dataSourceFactory: DefaultDataSource.Factory): ProgressiveMediaSource {
    val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
    return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
}