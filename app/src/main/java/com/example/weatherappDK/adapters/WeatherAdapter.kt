package com.example.weatherappDK.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappDK.R
import com.example.weatherappDK.databinding.ListItemBinding
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*

//Заполнение карточки с погодой
class WeatherAdapter : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)

        fun bind(item: WeatherModel) = with(binding){
            tvDate.text = formatDate(item.time)
            tvTempMax.text = item.maxTemp.myFormat()
            tvTempMin.text = item.minTemp.myFormat()
            tvCurrTemp.text = item.currentTemp
            tvCondition.text = changeDeskr(item.condition)
            Picasso.get().load("https:"+item.imageURL).into(tvImage)
        }

        //Функция для отображения градусов цельсия
        private fun String.myFormat(n: Int = 0) = String.format(Locale.getDefault(),"%.${n}f°", this.toFloat())

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
        //Функция перевода даты
        fun formatDate(dateStr: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

            return try {
                val date = inputFormat.parse(dateStr)
                val calendar = Calendar.getInstance()
                calendar.time = date
                outputFormat.format(calendar.time)
            } catch (e: ParseException) {
                // Возвращаем пустую строку или дефолтное значение, если дата не распарсилась
                ""
            }
        }
    }


    class Comparator: DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item2,parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}