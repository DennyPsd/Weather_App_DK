package com.example.weatherappDK.data

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.weatherappDK.R
import com.example.weatherappDK.adapters.VpAdapter
import com.example.weatherappDK.databinding.FragmentMainBinding
import com.example.weatherappDK.ui.DaysFragment
import com.example.weatherappDK.ui.DialogManager
import com.example.weatherappDK.ui.HoursFragment
import com.example.weatherappDK.ui.TapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject
import java.util.Calendar


class MainFragment : Fragment() {

//    private lateinit var fLocationClient: FusedLocationProviderClient



    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: ManiViewModel by activityViewModels()

    //Подключение binding при создании
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    //Проверка разрешений и инициализация функции для слайдера. Запуск мэин функций
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocation()
        init()
        updateCurrentCard()
        pLauncher = permissionListener()
        //requestWeatherData("Omsk")
        getData()
    }

//Слайдер карточек
    //Массив для карточек прогноза Дни/Часы
    private val fList = listOf(
    DaysFragment(),
    TapFragment()
    )
    private val tList = listOf(
        HoursFragment()
    )

    //Получение локации при разворачивании прилки
    override fun onResume() {
        super.onResume()
        checkLocation()
        checkPermission(pLauncher)
    }

    //Функция адаптера для слайдера
    private fun init() = with(binding){
        //fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        dotsIndicator.setViewPager2(vp)

        val adapter2 = VpAdapter(activity as FragmentActivity, tList)
        vp2.adapter = adapter2

        ibSync.setOnClickListener{
            checkLocation()
        }
        ibSearch.setOnClickListener{
            DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    if (name != null) {
                        requestWeatherData(name)
                    }
                }
            })
        }
    }

    //Функция обновления данных на верхней карточке
    private fun updateCurrentCard() = with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            tvDateMain.text = getData()
            //tvCountry.text = changeCity(it.city)
            tvCountry.text = it.city
            tvDeskr.text = changeDeskr(it.condition)
            tvTempMain.text = "${Math.round(it.currentTemp.toFloat())} ℃"
            gifLoader(changeDeskr(it.condition))

        }
    }

    //Функция перевода описания
    private fun changeDeskr(deskr: String):String{
        var nowDeskr = ""
        when (deskr.trim().lowercase()){
            "sunny" -> nowDeskr = "Солнечно"
            "clear" -> nowDeskr = "Ясно"
            "patchy rain nearby","light freezing rain" -> nowDeskr = "Местами дождь"
            "clouds", "cloudy", "partly cloudy" -> nowDeskr = "Облачно"
            "overcast" -> nowDeskr = "Пасмурно"
            "snow", "heavy snow","moderate snow","light snow" -> nowDeskr = "Снег"
        }
        return nowDeskr
    }

    //Тестовая функция получения даты
    private fun getData(): String{
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val min = Calendar.getInstance().get(Calendar.MINUTE)

        val nowDay = when (dayOfWeek) {
            1 -> "Воскресенье"
            2 -> "Понедельник"
            3 -> "Вторник"
            4 -> "Среда"
            5 -> "Четверг"
            6 -> "Пятница"
            7 -> "Суббота"
            else -> ""
        }

        val nowMonth = when (month) {
            1 -> "Февраля"
            2 -> "Марта"
            3 -> "Апреля"
            4 -> "Мая"
            5 -> "Июня"
            6 -> "Июля"
            7 -> "Августа"
            8 -> "Сентября"
            9 -> "Октября"
            10 -> "Ноября"
            11 -> "Декабря"
            12 -> "Января"
            else -> ""
        }

        return nowDay + ", " + day + " " + nowMonth
    }

    //Функция загрузки GIF по прогнозу ДОДЕЛАТЬ условия на каждое описание
    private fun gifLoader(condition: String) = with(binding){
        when (condition) {
            "Солнечно", "Ясно" ->  Glide.with(this@MainFragment).load(R.drawable.gif_clear).into(tvImageMain)
            "Местами дождь" ->  Glide.with(this@MainFragment).load(R.drawable.gif_mest_rain).into(tvImageMain)
            "Облачно" ->  Glide.with(this@MainFragment).load(R.drawable.gif_mest_obl).into(tvImageMain)
            "Пасмурно" ->  Glide.with(this@MainFragment).load(R.drawable.gif_obl).into(tvImageMain)
            "Снег" ->  Glide.with(this@MainFragment).load(R.drawable.gif_snow).into(tvImageMain)
        else -> {}
        }
    }

    //Запрос погоды с API
     private fun requestWeatherData(city: String){
        RequestData.requestWeatherDataClass(
            context = requireContext(),
            city = city,
            onSuccess = { result ->
                WeatherDataParser.parseWeatherData(result, model)
            },
            onError = { error ->
                Log.d("MyLog", "Ошибка запроса: ${error.message}")
            }
        )
    }
    //Отображение фрагмента
    companion object {
        fun newInstance() = MainFragment()
    }
}
