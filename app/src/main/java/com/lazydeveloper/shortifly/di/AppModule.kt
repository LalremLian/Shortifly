package com.lazydeveloper.shortifly.di

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.lazydeveloper.shortifly.network.ApiClient
import com.lazydeveloper.shortifly.data.api.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return ApiClient.getRetrofit()
    }

    @Singleton
    @Provides
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Singleton
    @Provides
    fun provideDataSourceFactory(@ApplicationContext context: Context): DefaultDataSource.Factory {
        return DefaultDataSource.Factory(context)
    }

//    @Singleton
//    @Provides
//    fun provideShortsAdapter(
//        @ApplicationContext context: Context,
//        exoPlayer: ExoPlayer,
//        dataSourceFactory: DefaultDataSource.Factory,
//        videoPreparedListener: ShortsAdapter.OnVideoPreparedListener
//    ): ShortsAdapter {
//        return ShortsAdapter(context, ArrayList(), exoPlayer, dataSourceFactory, videoPreparedListener)
//    }
}