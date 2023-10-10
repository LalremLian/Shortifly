package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.ui.adapters.SearchListAdapter
import com.lazydeveloper.shortifly.coroutine.Resource
import com.lazydeveloper.shortifly.databinding.FragmentHomeBinding
import com.lazydeveloper.shortifly.interfaces.OnFragmentInteractionListener
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.viewmodels.HomeFragViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragViewModel by viewModels()
    private var listener: OnFragmentInteractionListener? = null
    private val postListAdapter: SearchListAdapter by lazy { SearchListAdapter(this) }

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            fetchFlowData()
        }
//        preparePlayer()
    }

    private fun initViews() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
    }

    private suspend fun fetchFlowData(){
        viewModel.getFlowData().collect{ resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state (e.g., show a progress bar)
                }
                is Resource.Success -> {
                    val data = resource.data

                    Log.e("MainActivity", "fetchFlowData: $data" )

//                    binding.textView.text = data.search_metadata.id.toString()
                    postListAdapter.submitList(resource.data.video_results)
                }
                is Resource.Error -> {
                    // Handle error state (e.g., show an error message)
//                    val errorMessage = resource.message // Access the error message here
                    // Display the error message to the user
                }
            }
        }
    }

    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(
            requireContext(),
        ).build()
        exoPlayer?.playWhenReady = true
//        binding.playerView.player = exoPlayer
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
//            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    }
    fun onItemClickListener(item: VideoResult) {

//        val gson = Gson()
//        val jsonString = gson.toJson(VideoResult)
        val navController = findNavController()
        //navigate to another fragment

        navController.navigate(R.id.action_homeFragment2_to_playerFragment2)




        Log.e("TAG2", "onItemClickListener: " )






//        openActivity<PostDetailsActivity> {
//            putExtra("data", Gson().toJson(item))
//        }


    }

}