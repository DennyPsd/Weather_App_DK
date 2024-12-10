package com.example.weatherappDK.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappDK.adapters.WeatherAdapter
import com.example.weatherappDK.data.ManiViewModel
import com.example.weatherappDK.databinding.FragmentDaysBinding

class DaysFragment : Fragment() {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: WeatherAdapter
    private val model: ManiViewModel by activityViewModels()

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }
    private fun initRcView() = with(binding){

        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = WeatherAdapter()
        rcView.adapter = adapter
    }

}