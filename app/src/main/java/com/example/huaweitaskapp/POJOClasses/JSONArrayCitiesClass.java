package com.example.huaweitaskapp.POJOClasses;

public class JSONArrayCitiesClass {
    int id;
    JSONCitiesClass city;

    public JSONArrayCitiesClass(int id, JSONCitiesClass city) {
        this.id = id;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public JSONCitiesClass getCity() {
        return city;
    }
}

