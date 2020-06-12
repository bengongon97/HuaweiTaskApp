package com.example.huaweitaskapp.POJOClasses;

public class CoordinateClass {
    String lat;
    String lon;

    public CoordinateClass(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
