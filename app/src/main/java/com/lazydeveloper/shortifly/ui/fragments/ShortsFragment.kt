package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.widget.ViewPager2
import com.lazydeveloper.shortifly.ExoPlayerItem
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.databinding.FragmentShortsBinding
import com.lazydeveloper.shortifly.ui.adapters.ShortsAdapter
import com.lazydeveloper.shortifly.utils.DataSet
import com.lazydeveloper.shortifly.utils.extensions.gone
import com.lazydeveloper.shortifly.utils.extensions.onClick
import com.lazydeveloper.shortifly.utils.extensions.visible
import com.lazydeveloper.shortifly.viewmodels.ShortsFragViewModel

class ShortsFragment : Fragment(), ShortsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentShortsBinding
    private lateinit var adapter: ShortsAdapter
    private val viewModel: ShortsFragViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShortsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        adapter = ShortsAdapter(
            DataSet.shortsList,
            object : ShortsAdapter.OnVideoPreparedListener {
                override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                    viewModel.addExoPlayerItem(exoPlayerItem)
                }
            }, this
        )

        binding.vpager.adapter = adapter
        binding.vpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var lastPlayer: ExoPlayer? = null

            override fun onPageSelected(position: Int) {
                lastPlayer?.pause()
                lastPlayer?.playWhenReady = false

                val player = viewModel.getExoPlayerItem(position)
                player?.playWhenReady = true
                player?.play()
                binding.imgPlay.gone()

                lastPlayer = player
            }
        })
        binding.imgSearch onClick {
            Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
        }
        binding.imgCamera onClick {
            Toast.makeText(requireContext(), "Camera", Toast.LENGTH_SHORT).show()
        }
        binding.imgMore onClick {
            Toast.makeText(requireContext(), "More", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer(binding.vpager.currentItem)
    }

    override fun onResume() {
        super.onResume()
        viewModel.playPlayer(binding.vpager.currentItem)
            binding.imgPlay.gone()
            binding.txtShorts.gone()
            binding.imgSubscriptions.gone()
            binding.txtLive.gone()
            binding.imgLive.gone()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAndClearPlayers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopAndClearPlayers()
    }

    override fun onItemClick(position: Int) {
        val index = viewModel.exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = viewModel.exoPlayerItems[index].exoPlayer
            if (player.isPlaying) {
                player.pause()
                binding.imgPlay.visible()
                binding.txtShorts.visible()
                binding.imgSubscriptions.visible()
                binding.txtLive.visible()
                binding.imgLive.visible()
                player.playWhenReady = false
            } else {
                player.playWhenReady = true
                player.play()
                binding.imgPlay.setImageResource(R.drawable.ic_pause)
                binding.imgPlay.gone()
                binding.txtShorts.gone()
                binding.imgSubscriptions.gone()
                binding.txtLive.gone()
                binding.imgLive.gone()
            }
        }
    }

    override fun onPlayerStateChange(position: Int) {
        val index = viewModel.exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = viewModel.exoPlayerItems[index].exoPlayer
            if (player.isPlaying) {
                player.pause()
                player.playWhenReady = false
            } else {
                player.playWhenReady = true
                player.play()
            }
        }

    }
}