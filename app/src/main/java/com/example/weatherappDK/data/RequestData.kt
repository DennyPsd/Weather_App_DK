package com.example.weatherappDK.data

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

object RequestData {

    private const val API_KEY = "51337e83a6284b8ebf481810241610"

    //Запрос погоды
    fun requestWeatherDataClass(
        context: Context,
        city: String,
        onSuccess: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val url = "https://api.weatherapi.com/v1/forecast.json?" +
                "key=$API_KEY" +
                "&q=$city" +
                "&days=7&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                onSuccess(response)
            },
            { error ->
                Log.e("RequestApi", "Error fetching weather data: ${error.message}")
                onError(error)
            }
        )
        // Настройка retry-политики для запроса
        request.retryPolicy = DefaultRetryPolicy(
            5000,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        // Добавление запроса в очередь
        queue.add(request)
    }

}


