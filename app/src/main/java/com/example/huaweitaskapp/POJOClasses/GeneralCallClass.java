package com.example.huaweitaskapp.POJOClasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeneralCallClass {
    double lat;
    double lon;
    String timezone;
    @SerializedName("daily")
    List<DailyClass> dailyClassList;

    public GeneralCallClass(double lat, double lon, String timezone, List<DailyClass> dailyClassList) {
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        this.dailyClassList = dailyClassList;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public List<DailyClass> getDailyClassList() {
        return dailyClassList;
    }
}
