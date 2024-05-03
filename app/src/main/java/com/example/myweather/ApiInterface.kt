package com.example.myweather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city:String,
        @Query("appId") appId:String,
        @Query("units") units:String
    ):Call<WeatherApp>
}