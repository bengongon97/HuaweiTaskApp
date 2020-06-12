package com.example.huaweitaskapp.POJOClasses;

public class JSONCitiesClass {
    String findname;
    String country;
    CoordinateClass coord;

    public JSONCitiesClass(String findname, String country, CoordinateClass coord) {
        this.findname = findname;
        this.country = country;
        this.coord = coord;
    }

    public String getFindname() {
        return findname;
    }

    public String getCountry() {
        return country;
    }

    public CoordinateClass getCoord() {
        return coord;
    }
}
