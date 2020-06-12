package com.example.huaweitaskapp.POJOClasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentWeatherClass {
    @SerializedName("weather")
    List<WeatherClass> weather;

    double temp;

    public CurrentWeatherClass(List<WeatherClass> weather, double temp) {
        this.weather = weather;
        this.temp = temp;
    }

    public List<WeatherClass> getWeather() {
        return weather;
    }

    public double getTemp() {
        return temp;
    }
}
