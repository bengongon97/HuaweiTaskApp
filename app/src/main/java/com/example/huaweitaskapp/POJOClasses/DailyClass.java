package com.example.huaweitaskapp.POJOClasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DailyClass {
    @SerializedName("weather")
    List<WeatherClass> weather;
    @SerializedName("temp")
    TempClass temperatures;

    public DailyClass(List<WeatherClass> weather, TempClass temperatures) {
        this.weather = weather;
        this.temperatures = temperatures;
    }

    public List<WeatherClass> getWeather() {
        return weather;
    }

    public TempClass getTemperatures() {
        return temperatures;
    }
}
