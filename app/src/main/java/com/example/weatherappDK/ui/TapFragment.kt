package com.example.weatherappDK.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherappDK.databinding.FragmentTapBinding

class TapFragment : Fragment() {
    private lateinit var binding: FragmentTapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTapBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

}