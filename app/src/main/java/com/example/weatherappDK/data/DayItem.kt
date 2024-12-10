package com.example.weatherappDK.data

data class DayItem(
    val city: String,
    val time: String,
    val condition: String,
    val imageURL: String,
    val currentTemp: String,
    val minTemp: String,
    val maxTemp: String,
    val hours: String
)
