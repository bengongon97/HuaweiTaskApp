package com.example.huaweitaskapp.POJOClasses;

public class CityArrayClass {
    int id;
    double lat;
    double lon;
    String findname;

    public CityArrayClass(int id, double lat, double lon, String findname) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.findname = findname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getFindname() {
        return findname;
    }

    public void setFindname(String findname) {
        this.findname = findname;
    }
}
