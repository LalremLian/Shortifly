package com.lazydeveloper.shortifly.ui.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.lazydeveloper.shortifly.ExoPlayerItem
import com.lazydeveloper.shortifly.data.models.Video
import com.lazydeveloper.shortifly.databinding.SingleVideoRowBinding

class ShortsAdapter(
    private val context: Context,
    private val videos: ArrayList<Video>,
    private val videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<ShortsAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VideoViewHolder(
            SingleVideoRowBinding.inflate(LayoutInflater.from(context), parent, false)
        )

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
        holder.setVideoPath(videos[position].videoUrl)
    }

    override fun getItemCount() = videos.size

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }

    inner class VideoViewHolder(private val binding: SingleVideoRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
        private val dataSourceFactory = DefaultDataSource.Factory(context)
        private lateinit var mediaSource: MediaSource

        init {
            binding.exoPlayer.player = exoPlayer
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("ExoPlayerError", error.toString())
                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                }

                @Deprecated("Deprecated in Java")
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    binding.pbLoading.visibility = if (playbackState == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                }
            })
        }

        fun bind(videoModel: Video) {
            binding.data = videoModel
        }

        fun setVideoPath(videoUrl: String) {
            exoPlayer.seekTo(0)
            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
        }
    }
}