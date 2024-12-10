package com.example.weatherappDK.data

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weatherappDK.ui.DialogManager
import com.example.weatherappDK.ui.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import android.provider.Settings
import android.content.Context
import android.location.LocationManager
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices

lateinit var fLocationClient: FusedLocationProviderClient


fun initializeLocationClient(context: Context) {
    fLocationClient = LocationServices.getFusedLocationProviderClient(context)
}

fun Fragment.checkPermission(pLauncher: ActivityResultLauncher<String>) {
    if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
        pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

fun Fragment.permissionListener(): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        Toast.makeText(activity, "Permission it $it", Toast.LENGTH_LONG).show()
    }
}

//Получение GPS
fun Fragment.getLocation() {
    val model: ManiViewModel by activityViewModels()
    initializeLocationClient(requireContext())
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{
//                Log.d("","${it.result.latitude},${it.result.longitude}")
            RequestData.requestWeatherDataClass(
                context = requireContext(),
                city = "${it.result.latitude},${it.result.longitude}",
                onSuccess = { result ->
                    WeatherDataParser.parseWeatherData(result, model)
                    Log.d("MyLog", "Ответ: ${result}")
                },
                onError = { error ->
                    Log.d("MyLog", "Ошибка запроса: ${error.message}")
                }
            )
        }
}

fun Fragment.isLocationEnabled(): Boolean {
    val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Fragment.checkLocation() {
    if (isLocationEnabled()) {
        getLocation()
    } else {
        DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
            override fun onClick(name: String?) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        })
    }
}

