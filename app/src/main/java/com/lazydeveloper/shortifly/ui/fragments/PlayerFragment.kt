package com.lazydeveloper.shortifly.ui.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.lazydeveloper.shortifly.utils.extensions.gone
import com.lazydeveloper.shortifly.utils.extensions.onClick
import com.lazydeveloper.shortifly.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private val postListAdapter: PlayerItemListAdapter by lazy { PlayerItemListAdapter() }
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var binding: FragmentPlayerBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
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
        }
    }

    @OptIn(UnstableApi::class) @SuppressLint("InflateParams", "CutPasteId")
    private fun initViews() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
        postListAdapter.submitList(DataSet.shortListForPlayerPage)

        // Observe the player state in the ViewModel
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            handlePlayerState(state)
        }
        viewModel.currentPosition.observe(viewLifecycleOwner) { currentPosition ->
            val txtCurrentTime = binding.videoPlayer.findViewById<TextView>(R.id.txtCurrentTime)
            txtCurrentTime.text = currentPosition.formatTime()
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

    @OptIn(UnstableApi::class) @SuppressLint("ResourceAsColor", "SetTextI18n", "InflateParams")
    private fun preparePlayer() {
        binding.progressbar.visible()
        viewModel.setMediaItem(Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
        binding.videoPlayer.player = viewModel.player
        binding.videoPlayer.useController = true

        val seekBar: SeekBar = binding.videoPlayer.findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = viewModel.player.duration
                    val newPosition = (progress * duration) / 100
                    viewModel.player.seekTo(newPosition)
                    updateSeekBar()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val playButton = binding.videoPlayer.findViewById<ImageView>(R.id.img_play)
        val rewindButton = binding.videoPlayer.findViewById<ImageView>(R.id.img_rewind)
        val forwardButton = binding.videoPlayer.findViewById<ImageView>(R.id.img_forward)
        val settingsButton = binding.videoPlayer.findViewById<ImageView>(R.id.imgSettings)
        val fullScreenButton = binding.videoPlayer.findViewById<ImageView>(R.id.imgFullScreen)
        val txtDuration = binding.videoPlayer.findViewById<TextView>(R.id.txtDuration)
        txtDuration.text = " / ${viewModel.videoDuration.formatTime()}"

        playButton onClick {
            if (viewModel.player.isPlaying) {
                viewModel.player.pause()
                playButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                viewModel.player.play()
                playButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
        rewindButton onClick {
            viewModel.player.seekTo(viewModel.player.currentPosition - 10000)
        }
        forwardButton onClick {
            viewModel.player.seekTo(viewModel.player.currentPosition + 10000)
        }
        settingsButton onClick {
            val dialogView = layoutInflater.inflate(R.layout.resolution_bottom_sheet, null)
            val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogView)
            val myButton = dialogView.findViewById<TextView>(R.id.txtResolution)
            myButton.setOnClickListener {
                viewModel.trackSelection(requireActivity())
                dialog.dismiss()
            }
            viewModel.selectedFormat.observe(viewLifecycleOwner) { format ->
                val quality = dialogView.findViewById<TextView>(R.id.txtQuality)
                quality.text = format
            }
            dialog.show()
        }
        fullScreenButton onClick {
            viewModel.toggleFullScreen(requireActivity())
        }

        seekBar.max = 100

        coroutineScope.launch {
            while (isActive) {
                updateSeekBar()
                delay(1000)
            }
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
    }

    fun updateSeekBar() {
        val seekBar: SeekBar = binding.videoPlayer.findViewById(R.id.seekBar)
        coroutineScope.launch {
            while (isActive) {
                val duration = viewModel.player.duration
                val currentPosition = viewModel.player.currentPosition
                val progress = if (duration > 0) (currentPosition * 100 / duration).toInt() else 0
                seekBar.progress = progress
                delay(1000) // Update every second
            }
        }
    }

    private fun handlePlayerState(state: Int) {
        when (state) {
            Player.STATE_IDLE -> {
                binding.progressbar.visible()
            }
            Player.STATE_READY -> {
                updateSeekBar()
                binding.progressbar.gone()
            }
            Player.STATE_BUFFERING -> {
                binding.progressbar.visible()
            }
            Player.STATE_ENDED -> {
                binding.progressbar.gone()
            }
        }
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
        coroutineScope.cancel()
    }
}