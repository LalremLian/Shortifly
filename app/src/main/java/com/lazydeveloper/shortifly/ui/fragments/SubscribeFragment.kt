package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazydeveloper.shortifly.databinding.FragmentSubscribeBinding
import com.lazydeveloper.shortifly.ui.adapters.ChannelListAdapter
import com.lazydeveloper.shortifly.ui.adapters.ContentListAdapter
import com.lazydeveloper.shortifly.utils.DataSet

class SubscribeFragment : Fragment() {
    private val channelListAdapter: ChannelListAdapter by lazy { ChannelListAdapter(this) }
    private val postListAdapter: ContentListAdapter by lazy { ContentListAdapter(this) }

    private lateinit var binding: FragmentSubscribeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscribeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    private fun initViews() {
        binding.rvSubscribe.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = channelListAdapter
        }
        channelListAdapter.submitList(DataSet.channelList)

        binding.rvSubscribeContent.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
        postListAdapter.submitList(DataSet.shortListForPlayerPage)
    }
}