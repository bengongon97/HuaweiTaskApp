package com.example.huaweitaskapp.POJOClasses;

import com.google.gson.annotations.SerializedName;

public class WeatherClass {
    @SerializedName("id")
    int id;
    @SerializedName("main")
    String main;
    @SerializedName("description")
    String description;

    public WeatherClass(int id, String main, String description) {
        this.id = id;
        this.main = main;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }
}
