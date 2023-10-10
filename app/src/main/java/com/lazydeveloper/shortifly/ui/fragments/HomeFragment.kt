package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.coroutine.Resource
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.FragmentHomeBinding
import com.lazydeveloper.shortifly.interfaces.OnFragmentInteractionListener
import com.lazydeveloper.shortifly.ui.adapters.SearchListAdapter
import com.lazydeveloper.shortifly.viewmodels.HomeFragViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragViewModel by viewModels()
    private var listener: OnFragmentInteractionListener? = null
    private val postListAdapter: SearchListAdapter by lazy { SearchListAdapter(this) }

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
    }

    private fun initViews() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
    }

    private suspend fun fetchFlowData() {
        viewModel.getFlowData().collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state (e.g., show a progress bar)
                }

                is Resource.Success -> {
                    val data = resource.data

                    Log.e("MainActivity", "fetchFlowData: $data")

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

    fun onItemClickListener(item: VideoResult) {
        val gson = Gson()
        val jsonString = gson.toJson(item)
        val navController = findNavController()

        navController.navigate(R.id.action_homeFragment2_to_playerFragment2, Bundle().apply {
            putString("data", jsonString)
        })
        Log.e("TAG2", "onItemClickListener: ")
    }

}