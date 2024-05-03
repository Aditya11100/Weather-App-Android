package com.example.myweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.myweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData( "Jamshedpur")
        searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityName,"a59719ddbf59fd6523935a7339ab13c9","metric")

        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(p0: Call<WeatherApp>, p1: Response<WeatherApp>) {
                 val response=p1.body()
                if(p1.isSuccessful){
                    val temperature=response?.main?.temp?.toString()
                    val humidity=response?.main?.humidity
                    val windSpeed=response?.wind?.speed
                    val sunRise=response?.sys?.sunrise?.toLong()
                    val sunSet=response?.sys?.sunset?.toLong()
                    val seaLevel=response?.main?.pressure
                    val condition=response?.weather?.firstOrNull()?.main?: "unknown"
                    val maxTemp=response?.main?.temp_max
                    val minTemp=response?.main?.temp_min

                    binding.textView3.text="$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max : $maxTemp °C"
                    binding.minTemp.text = "Min : $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${sunRise?.let { time(it) }}"
                    binding.sunset.text = "${sunSet?.let { time(it) }}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityName"
//                    Log.d("http","on response: ${temperature}")
                    changeImage(condition)
                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImage(conditions:String) {
        when (conditions){
            "Haze","Partly Clouds", "Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Clear","Sunny","Clear Sky"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Showers","Heavy Rain","Moderate Rain","Drizzle","Light Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return  sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
    }
}