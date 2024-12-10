package com.example.weatherappDK.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappDK.adapters.WeatherHourlyAdapter
import com.example.weatherappDK.data.WeatherDataParser
import com.example.weatherappDK.data.ManiViewModel
import com.example.weatherappDK.databinding.FragmentHoursBinding


class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherHourlyAdapter
    private val model: ManiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
       model.liveDataCurrent.observe(viewLifecycleOwner){
           adapter.submitList(WeatherDataParser.getHoursList(it))
        }
    }

    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherHourlyAdapter()
        rcView.adapter = adapter
    }

}