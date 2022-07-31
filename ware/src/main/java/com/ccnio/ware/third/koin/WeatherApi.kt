package com.ccnio.ware.third.koin

interface WeatherApi {
    fun getWeather(): String
}

class WeatherApiImplCN : WeatherApi {
    override fun getWeather(): String {
        return "cn"
    }
}

class WeatherApiImplUS : WeatherApi {
    override fun getWeather(): String {
        return "us"
    }
}
