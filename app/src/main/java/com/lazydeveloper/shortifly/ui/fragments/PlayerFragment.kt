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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.FragmentPlayerBinding
import com.lazydeveloper.shortifly.player.PlayerViewModel
import com.lazydeveloper.shortifly.ui.adapters.PlayerItemListAdapter
import com.lazydeveloper.shortifly.utils.DataSet
import com.lazydeveloper.shortifly.utils.extensions.formatTime
import com.lazydeveloper.shortifly.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private val postListAdapter: PlayerItemListAdapter by lazy { PlayerItemListAdapter(this) }

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
            viewModel.setPlayerVisibility()
        }
    }

    private fun initViews() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
        postListAdapter.submitList(DataSet.shortListForPlayerPage)

        // Observe the player visibility state in the ViewModel
        viewModel.playerVisibility.observe(viewLifecycleOwner) { isPlayerVisible ->
            updatePlayerVisibility(isPlayerVisible)
        }

        binding.playerOverlay onClick {
            // Trigger the ViewModel to toggle the visibility
            viewModel.togglePlayerVisibility()
        }

        // Observe the player state in the ViewModel
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            handlePlayerState(state)
        }
    }

    fun onItemClickListener(item: VideoResult) {}

    @SuppressLint("ResourceAsColor")
    private fun preparePlayer() {
        viewModel.setMediaItem(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
        binding.videoPlayer.player = viewModel.player
        binding.videoPlayer.useController = false
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

    private fun handlePlayerState(state: Int) {
        when (state) {
            Player.STATE_IDLE -> {
                // Handle idle state
            }
            Player.STATE_READY -> {
                val duration = viewModel.player.duration
                val currentPosition = viewModel.player.currentPosition
                updateUiWithCurrentPosition(currentPosition)
                updateSeekBar()
            }
            Player.STATE_BUFFERING -> {
                // Handle buffering state
            }
            // Add other states as needed
        }
    }

    private fun updateUiWithCurrentPosition(currentPosition: Long) {
        // Update your TextView or any other UI element with the current position
        // For example, if you have a TextView named currentPositionTextView:
        binding.txtCurrentTime.text = currentPosition.formatTime()
    }

    @SuppressLint("ResourceAsColor")
    private fun updatePlayerVisibility(isPlayerVisible: Boolean) {
        if (isPlayerVisible) {
            binding.playerOverlay.setBackgroundColor(R.color.black)
            binding.imgPlay.visibility = View.VISIBLE
            binding.imgRewind.visibility = View.VISIBLE
            binding.imgForward.visibility = View.VISIBLE
            binding.imgSettings.visibility = View.VISIBLE
            binding.imgFullScreen.visibility = View.VISIBLE
            binding.txtCurrentTime.visibility = View.VISIBLE
            binding.txtDuration.visibility = View.VISIBLE
        } else {
            binding.playerOverlay.setBackgroundColor(Color.TRANSPARENT)
            binding.imgPlay.visibility = View.GONE
            binding.imgRewind.visibility = View.GONE
            binding.imgForward.visibility = View.GONE
            binding.imgSettings.visibility = View.GONE
            binding.imgFullScreen.visibility = View.GONE
            binding.txtCurrentTime.visibility = View.GONE
            binding.txtDuration.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
    }
}