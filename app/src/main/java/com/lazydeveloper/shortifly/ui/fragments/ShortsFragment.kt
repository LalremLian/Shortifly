package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.widget.ViewPager2
import com.lazydeveloper.shortifly.ExoPlayerItem
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.databinding.FragmentShortsBinding
import com.lazydeveloper.shortifly.ui.adapters.ShortsAdapter
import com.lazydeveloper.shortifly.utils.DataSet

class ShortsFragment : Fragment(), ShortsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentShortsBinding
    private lateinit var adapter: ShortsAdapter
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()

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
        adapter = ShortsAdapter(requireContext(),
            DataSet.shortsList,
            object : ShortsAdapter.OnVideoPreparedListener {
            override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                exoPlayerItems.add(exoPlayerItem)
            }
        }, this)

        binding.vpager.adapter = adapter

        binding.vpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var lastPlayer: ExoPlayer? = null

            override fun onPageSelected(position: Int) {
                lastPlayer?.pause()
                lastPlayer?.playWhenReady = false

                val player = exoPlayerItems.firstOrNull { it.position == position }?.exoPlayer
                player?.playWhenReady = true
                player?.play()
                binding.imgPlay.visibility = View.GONE

                lastPlayer = player
            }
        })
        binding.imgSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
        }
        binding.imgCamera.setOnClickListener {
            Toast.makeText(requireContext(), "Camera", Toast.LENGTH_SHORT).show()
        }
        binding.imgMore.setOnClickListener {
            Toast.makeText(requireContext(), "More", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("ShortsFragment","onPause")
        val currentItem = binding.vpager.currentItem
        exoPlayerItems.firstOrNull { it.position == currentItem }?.exoPlayer?.apply {
            pause()
            playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
            binding.imgPlay.visibility = View.GONE
            binding.txtShorts.visibility = View.GONE
            binding.imgSubscriptions.visibility = View.GONE
            binding.txtLive.visibility = View.GONE
            binding.imgLive.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(exoPlayerItems.isEmpty()){
            for (item in exoPlayerItems){
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("ShortsFragment","onDestroyView")
        for (item in exoPlayerItems){
            val player = item.exoPlayer
            player.stop()
            player.clearMediaItems()
        }
    }

    override fun onItemClick(position: Int) {
        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            if (player.isPlaying) {
                player.pause()
                binding.imgPlay.visibility = View.VISIBLE
                binding.txtShorts.visibility = View.VISIBLE
                binding.imgSubscriptions.visibility = View.VISIBLE
                binding.txtLive.visibility = View.VISIBLE
                binding.imgLive.visibility = View.VISIBLE
                player.playWhenReady = false
            } else {
                player.playWhenReady = true
                player.play()
                binding.imgPlay.setImageResource(R.drawable.ic_pause)
                binding.imgPlay.visibility = View.GONE
                binding.txtShorts.visibility = View.GONE
                binding.imgSubscriptions.visibility = View.GONE
                binding.txtLive.visibility = View.GONE
                binding.imgLive.visibility = View.GONE
            }
        }
    }

    override fun onPlayerStateChange(position: Int) {
        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            if (player.isPlaying) {
                player.pause()
//                binding.imgPlay.visibility = View.VISIBLE
                player.playWhenReady = false
            } else {
                player.playWhenReady = true
                player.play()
//                binding.imgPlay.setImageResource(R.drawable.ic_pause)
//                binding.imgPlay.visibility = View.GONE
            }
        }

    }
}