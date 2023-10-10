package com.lazydeveloper.shortifly.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lazydeveloper.shortifly.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater)
        return binding.root
    }

}