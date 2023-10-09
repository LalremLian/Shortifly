package com.lazydeveloper.shortifly.fragments

import com.lazydeveloper.shortifly.adapter.ShortsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lazydeveloper.shortifly.ExoPlayerItem
import com.lazydeveloper.shortifly.databinding.FragmentShortsBinding
import com.lazydeveloper.shortifly.model.Video
import com.lazydeveloper.shortifly.utils.DataSet

class ShortsFragment : Fragment() {

    private lateinit var binding: FragmentShortsBinding
    private lateinit var adapter: ShortsAdapter
    private val videos = ArrayList<Video>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        })

        binding.vpager.adapter = adapter

        binding.vpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                }
                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = exoPlayerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
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

}