package com.lazydeveloper.shortifly.di

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PlayerModule {

    @OptIn(UnstableApi::class) @Provides
    @ViewModelScoped
    fun provideTrackSelector(app: Application): DefaultTrackSelector {
        return DefaultTrackSelector(app)
    }

    @OptIn(UnstableApi::class) @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application, trackSelector: DefaultTrackSelector): ExoPlayer {  // Change this line
        return ExoPlayer.Builder(app)
            .setTrackSelector(trackSelector)
            .build()
    }
}