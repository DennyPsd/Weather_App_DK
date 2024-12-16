package com.example.weatherappDK.data

import com.example.weatherappDK.adapters.WeatherModel
import org.json.JSONArray
import org.json.JSONObject



object WeatherDataParser {
    // Парсинг погоды по часам
    fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(wItem.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                time = (hoursArray[i] as JSONObject).getString("time"),
                condition = (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                currentTemp = (hoursArray[i] as JSONObject).getString("temp_c"),
                imageURL = (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"),
            )
            list.add(item)
        }
        return list
    }

    // Парсинг текущей погоды
    fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel): WeatherModel {
        return WeatherModel(
            city = mainObject.getJSONObject("location").getString("region"),
            time = mainObject.getJSONObject("location").getString("localtime"),
            condition = mainObject.getJSONObject("current").getJSONObject("condition")
                .getString("text"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            imageURL = "",
            hours = weatherItem.hours
        )
    }

    // Парсинг погоды по дням
    fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val cityName = mainObject.getJSONObject("location").getString("name")

        for (i in 0 until daysArray.length()) {
            val dayObject = daysArray[i] as JSONObject
            val weatherModel = WeatherModel(
                city = cityName,
                time = dayObject.getString("date"),
                condition = dayObject.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                currentTemp = "",
                maxTemp = dayObject.getJSONObject("day").getString("maxtemp_c"),
                minTemp = dayObject.getJSONObject("day").getString("mintemp_c"),
                imageURL = dayObject.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                hours = dayObject.getJSONArray("hour").toString()
            )
            list.add(weatherModel)
        }

        return list
    }

    //Запись данных в модели
    fun parseWeatherData(result: String, model: ManiViewModel) {
        val mainObject = JSONObject(result)
        val daysList = parseDays(mainObject)
        val currentData = parseCurrentData(mainObject, daysList[0])
        model.liveDataList.value = daysList
        model.liveDataCurrent.value = currentData
    }
}


