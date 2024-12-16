package com.example.weatherappDK.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappDK.R
import com.example.weatherappDK.databinding.ListItem2Binding
import com.squareup.picasso.Picasso

//Заполнение первой карточки с почасовым прогнозом
class WeatherHourlyAdapter : ListAdapter<WeatherModel, WeatherHourlyAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ListItem2Binding.bind(view)

        fun bind(item: WeatherModel) = with(binding) {
            tvDate.text = item.time.split(" ")[1].replaceFirst("^0".toRegex(), "")
            tvCurrTemp.text = "${Math.round(item.currentTemp.toFloat())}" + "°"
            tvCondition.text = changeDeskr(item.condition)
            Picasso.get().load("https:" + item.imageURL).into(tvImage)
        }

        //Функция перевода описания
        private fun changeDeskr(deskr: String) =
            when (deskr.trim().lowercase()) {
                "sunny" -> "Солнечно"
                "clear" -> "Ясно"
                "patchy rain nearby", "light freezing rain" -> "Местами дождь"
                "clouds", "cloudy", "partly cloudy" -> "Облачно"
                "overcast" -> "Пасмурно"
                "snow", "heavy snow", "moderate snow", "light snow" -> "Снег"
                else -> ""
            }
    }


    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}
