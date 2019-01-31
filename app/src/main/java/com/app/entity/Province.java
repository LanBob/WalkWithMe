package com.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * private City[] city;
 */
public class Province {
    @SerializedName("name")
    private String name;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @SerializedName("city")
    private List<City> cities;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
