package com.lazydeveloper.shortifly.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.FragmentPlayerBinding
import com.lazydeveloper.shortifly.player.PlayerViewModel
import com.lazydeveloper.shortifly.ui.adapters.PlayerItemListAdapter
import com.lazydeveloper.shortifly.utils.DataSet
import com.lazydeveloper.shortifly.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private val postListAdapter: PlayerItemListAdapter by lazy { PlayerItemListAdapter(this) }

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var playerVisibility = false

    private val viewModel: PlayerViewModel by viewModels()

    private lateinit var seekBarHandler: Handler
    private lateinit var seekBarRunnable: Runnable

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        getData()
        return binding.root
    }

    private fun getData() {
        val gson = Gson()
        val data = arguments?.getString("data")
        val videoResult = gson.fromJson(data, VideoResult::class.java)

        binding.txtTitle.text = videoResult.title
        binding.txtChannelName.text = videoResult.author
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            preparePlayer()
        }
    }
    private fun initViews() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
        postListAdapter.submitList(DataSet.shortListForPlayerPage)
    }

    fun onItemClickListener(item: VideoResult) {}
    @SuppressLint("ResourceAsColor")
    private fun preparePlayer() {
        viewModel.setMediaItem(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        exoPlayer?.playWhenReady = true
        binding.videoPlayer.player = viewModel.player
        binding.videoPlayer.useController = false

        binding.videoPlayer.player!!.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_READY) {
                    val duration = viewModel.player.duration
                    val currentPosition = viewModel.player.currentPosition
                    updateUiWithCurrentPosition(currentPosition)
                    updateSeekBar()
                } else if (state == Player.STATE_BUFFERING) {

                }
            }
        })

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = viewModel.player.duration
                    val newPosition = (progress * duration) / 100
                    viewModel.player.seekTo(newPosition)
                    binding.txtDuration.text = duration.toString()
                    updateSeekBar()
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

        binding.overlay.setOnClickListener {

            playerVisibility = !playerVisibility

            if (playerVisibility) {
                binding.overlay.setBackgroundColor(R.color.black)
                binding.imgPlay.visibility = View.VISIBLE
                binding.imgRewind.visibility = View.VISIBLE
                binding.imgForward.visibility = View.VISIBLE
                binding.imgSettings.visibility = View.VISIBLE
                binding.imgFullScreen.visibility = View.VISIBLE
                binding.txtCurrentTime.visibility = View.VISIBLE
                binding.txtDuration.visibility = View.VISIBLE
            } else {
                binding.overlay.setBackgroundColor(Color.TRANSPARENT)
                binding.imgPlay.visibility = View.GONE
                binding.imgRewind.visibility = View.GONE
                binding.imgForward.visibility = View.GONE
                binding.imgSettings.visibility = View.GONE
                binding.imgFullScreen.visibility = View.GONE
                binding.txtCurrentTime.visibility = View.GONE
                binding.txtDuration.visibility = View.GONE
            }
        }

        binding.imgPlay onClick {
            if (viewModel.player.isPlaying) {
                viewModel.player.pause()
                binding.imgPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {
                viewModel.player.play()
                binding.imgPlay.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

    }
    fun updateSeekBar() {
        val duration = viewModel.player.duration
        val currentPosition = viewModel.player.currentPosition
        val progress = if (duration > 0) (currentPosition * 100 / duration).toInt() else 0
        binding.seekBar.progress = progress

        // Schedule the next update
        seekBarHandler.postDelayed(seekBarRunnable, 1000) // Update every second
    }

    private fun updateUiWithCurrentPosition(currentPosition: Long) {
        // Update your TextView or any other UI element with the current position
        // For example, if you have a TextView named currentPositionTextView:
        binding.txtCurrentTime.text = formatTime(currentPosition)
    }

    // Function to format time (you can customize this based on your needs)
    private fun formatTime(timeInMillis: Long): String {
        // Format time as HH:mm:ss or in a way that suits your needs
        val seconds = timeInMillis / 1000
        val minutes = seconds / 60
//        val hours = minutes / 60

        return String.format("%02d:%02d", seconds % 60, minutes % 60)
    }
    private fun releasePlayer(){
        viewModel.player.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
    }
}