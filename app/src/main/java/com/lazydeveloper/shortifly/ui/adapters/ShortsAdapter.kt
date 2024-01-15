package com.lazydeveloper.shortifly.ui.adapters

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
    private val videoPreparedListener: OnVideoPreparedListener,
    private val itemClickListener: OnItemClickListener? = null
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

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPlayerStateChange(position: Int)
    }

    inner class VideoViewHolder(private val binding: SingleVideoRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
        private val dataSourceFactory = DefaultDataSource.Factory(context)
        private lateinit var mediaSource: MediaSource

        private var seekBarHandler: Handler
        private var seekBarRunnable: Runnable

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
                    binding.pbLoading.visibility =
                        if (playbackState == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                }
            })

            // Initialize SeekBar and its handler for real-time tracking
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val duration = exoPlayer.duration
                        val newPosition = (progress * duration) / 100
                        exoPlayer.seekTo(newPosition)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    updateSeekBar()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Handle seek bar touch stop
                }
            })
            binding.seekBar.max = 100
            seekBarHandler = Handler()
            seekBarRunnable = Runnable { updateSeekBar() }

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(position = absoluteAdapterPosition)
            }
            binding.exoPlayer.setOnClickListener {
                Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show()
                itemClickListener?.onPlayerStateChange(position)
            }
            binding.imgLikes.setOnClickListener {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show()
            }
            binding.imgDislikes.setOnClickListener {
                Toast.makeText(context, "Dislike", Toast.LENGTH_SHORT).show()
            }
            binding.imgComments.setOnClickListener {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show()
            }
            binding.imgShare.setOnClickListener {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show()
            }
            binding.imgRemix.setOnClickListener {
                Toast.makeText(context, "Remix", Toast.LENGTH_SHORT).show()
            }
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
            binding.seekBar.max = 100
            seekBarHandler.postDelayed(seekBarRunnable, 1000)

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

            // Schedule the next update
            seekBarHandler.postDelayed(seekBarRunnable, 1000) // Update every second
        }
    }
}