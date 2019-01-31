package com.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class City {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    @SerializedName("area")
    private List<String> area;
}
