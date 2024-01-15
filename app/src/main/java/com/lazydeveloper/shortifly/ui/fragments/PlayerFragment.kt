package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.gson.Gson
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.FragmentPlayerBinding
import com.lazydeveloper.shortifly.ui.adapters.PlayerItemListAdapter
import com.lazydeveloper.shortifly.utils.DataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private val postListAdapter: PlayerItemListAdapter by lazy { PlayerItemListAdapter(this) }

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        preparePlayer()
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
    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(
            requireContext(),
        ).build()
        exoPlayer?.playWhenReady = true
        binding.videoPlayer.player = exoPlayer
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem = MediaItem.fromUri(URL)
        val mediaSource =
            DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer?.setMediaSource(mediaSource)
        exoPlayer?.seekTo(playbackPosition)
        exoPlayer?.playWhenReady = playWhenReady
        exoPlayer?.prepare()
    }

    private fun  relasePlayer(){
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        relasePlayer()
    }

    override fun onPause() {
        super.onPause()
        relasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        relasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        relasePlayer()
    }

    companion object{
        const val URL =
            "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"
    }

}