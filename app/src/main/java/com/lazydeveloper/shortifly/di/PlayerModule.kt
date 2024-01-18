package com.lazydeveloper.shortifly.di

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.google.android.gms.cast.framework.CastContext
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
    fun provideVideoPlayer(app: Application): Player {
        val trackSelector = DefaultTrackSelector(app)
        return ExoPlayer.Builder(app)
            .setTrackSelector(trackSelector)
            .build()
    }

    // Cast
    @Provides
    @ViewModelScoped
    fun provideCastContext(app: Application): CastContext {
        return CastContext.getSharedInstance(app)
    }

    @Provides
    @ViewModelScoped
    fun provideMediaRouter(app: Application): androidx.mediarouter.media.MediaRouter {
        return androidx.mediarouter.media.MediaRouter.getInstance(app)
    }
}