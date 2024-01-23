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
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.FragmentPlayerBinding
import com.lazydeveloper.shortifly.player.PlayerViewModel
import com.lazydeveloper.shortifly.ui.adapters.PlayerItemListAdapter
import com.lazydeveloper.shortifly.ui.home.MainActivity
import com.lazydeveloper.shortifly.utils.DataSet
import com.lazydeveloper.shortifly.utils.extensions.formatTime
import com.lazydeveloper.shortifly.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private val postListAdapter: PlayerItemListAdapter by lazy { PlayerItemListAdapter() }

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

    @OptIn(UnstableApi::class) override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            preparePlayer()
            viewModel.setPlayerVisibility()
        }
    }

    @OptIn(UnstableApi::class) @SuppressLint("InflateParams")
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

        binding.imgSettings onClick {
            val dialogView = layoutInflater.inflate(R.layout.resolution_bottom_sheet, null)
            val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogView)
            val myButton = dialogView.findViewById<TextView>(R.id.txtResolution)
            myButton.setOnClickListener {
                viewModel.trackSelection(requireActivity())
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.imgFullScreen onClick {
            viewModel.toggleFullScreen(requireActivity())
        }

        // Observe the player state in the ViewModel
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            handlePlayerState(state)
        }

        viewModel.currentPosition2.observe(viewLifecycleOwner) { currentPosition ->
            // Update your UI with the current watch time
            binding.txtCurrentTime.text = currentPosition.formatTime()
            // Update your TextView or other UI elements
        }
    }

    private fun handleFullScreen(isFullScreen: Boolean) {
        val layoutParams = if (isFullScreen) {
            ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT to 610
        }

        // Adjust your Media3 view's layout parameters
        binding.videoPlayer.layoutParams = binding.videoPlayer.layoutParams.apply {
            width = layoutParams.first
            height = layoutParams.second
        }
    }

    fun onItemClickListener(item: VideoResult) {}

    @OptIn(UnstableApi::class) @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun preparePlayer() {
        viewModel.setMediaItem(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"))
        binding.videoPlayer.player = viewModel.player
        binding.videoPlayer.useController = false
        binding.txtDuration.text = " / ${viewModel.videoDuration.formatTime()}"
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = viewModel.player.duration
                    val newPosition = (progress * duration) / 100
                    viewModel.player.seekTo(newPosition)
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
            viewModel.playPause()
            if (viewModel.player.isPlaying) {
                binding.imgPlay.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                binding.imgPlay.setImageResource(android.R.drawable.ic_media_play)
            }
        }
        binding.imgForward onClick {
            viewModel.forward(10000)
        }
        binding.imgRewind onClick {
            viewModel.rewind(10000)
        }
        val mainActivity = requireActivity() as MainActivity
        // Observe the full-screen state
        viewModel.isFullScreen.observe(viewLifecycleOwner) { isFullScreen ->
            isFullScreen?.let { handleFullScreen(it) }
            if (isFullScreen) {
                mainActivity.setCustomHeaderVisibility(false)
            } else {
                mainActivity.setCustomHeaderVisibility(true)
            }
        }
        viewModel.isFullScreen.observe(viewLifecycleOwner) { isFullScreen ->
            isFullScreen?.let { handleFullScreen(it) }
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
                updateSeekBar()
            }
            Player.STATE_BUFFERING -> {
                // Handle buffering state
            }
            // Add other states as needed
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun updatePlayerVisibility(isPlayerVisible: Boolean) {
        val visibility = if (isPlayerVisible) View.VISIBLE else View.GONE

        binding.playerOverlay.setBackgroundColor(
            if (isPlayerVisible) R.color.black
            else Color.TRANSPARENT
        )

        binding.imgPlay.visibility = visibility
        binding.imgRewind.visibility = visibility
        binding.imgForward.visibility = visibility
        binding.imgSettings.visibility = visibility
        binding.imgFullScreen.visibility = visibility
        binding.txtCurrentTime.visibility = visibility
        binding.txtDuration.visibility = visibility
        binding.seekBar.visibility = visibility
    }

    @OptIn(UnstableApi::class) override fun onStop() {
        super.onStop()
        viewModel.releasePlayer()
    }

    @OptIn(UnstableApi::class) override fun onPause() {
        super.onPause()
        viewModel.releasePlayer()
    }

    @OptIn(UnstableApi::class) override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    @OptIn(UnstableApi::class) override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
    }
}