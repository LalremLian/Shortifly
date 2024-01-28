
package com.lazydeveloper.shortifly.ui.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.recyclerview.widget.RecyclerView
import com.lazydeveloper.shortifly.ExoPlayerItem
import com.lazydeveloper.shortifly.data.models.Video
import com.lazydeveloper.shortifly.databinding.SingleVideoRowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShortsAdapter(
    private val videos: ArrayList<Video>,
    private val videoPreparedListener: OnVideoPreparedListener,
    private val itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<ShortsAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VideoViewHolder(
            SingleVideoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
        holder.setVideoPath(videos[position].videoUrl)
    }

    override fun getItemCount() = videos.size

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPlayerStateChange(position: Int)
    }

    inner class VideoViewHolder(private val binding: SingleVideoRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val scope = CoroutineScope(Dispatchers.Main)
        var seekBarJob: Job? = null

        private val exoPlayer: ExoPlayer = ExoPlayer.Builder(binding.root.context).build()
        private val dataSourceFactory = DefaultDataSource.Factory(binding.root.context)
        private lateinit var mediaSource: MediaSource

        init {
            initExoPlayer()
            initSeekBar()
            initClickListeners()
        }

        private fun initExoPlayer() {
            binding.exoPlayer.player = exoPlayer
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("ExoPlayerError", error.toString())
                    Toast.makeText(binding.root.context, "Can't play this video", Toast.LENGTH_SHORT).show()
                }

                @Deprecated("Deprecated in Java")
                @SuppressLint("UnsafeOptInUsageError")
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    binding.pbLoading.visibility =
                        if (playbackState == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                }
            })
        }

        private fun initSeekBar() {
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val duration = exoPlayer.duration
                        val newPosition = (progress * duration) / 100
                        exoPlayer.seekTo(newPosition)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            binding.seekBar.max = 100
        }

        private fun initClickListeners() {
             binding.root.setOnClickListener {
                 itemClickListener?.onItemClick(position = absoluteAdapterPosition)
             }
        }

        fun bind(videoModel: Video) {
            binding.data = videoModel
        }

        @SuppressLint("UnsafeOptInUsageError")
        fun setVideoPath(videoUrl: String) {
            exoPlayer.seekTo(0)
            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            seekBarJob = scope.launch {
                while (true) {
                    updateSeekBar()
                    delay(1000)
                }
            }

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }
            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
        }

        private fun updateSeekBar() {
            val duration = exoPlayer.duration
            val currentPosition = exoPlayer.currentPosition
            val progress = if (duration > 0) (currentPosition * 100 / duration).toInt() else 0
            binding.seekBar.progress = progress
        }
    }
    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.seekBarJob?.cancel()
    }
}