package com.proj.Model.TimeAndWeather;

public enum Weather {
    SUNNY("Sunny"),
    SNOWY("Snowy"),
    RAINY("Rainy"),
    STORMY("Stormy");
    private String weather;
    Weather(String weather) {
        this.weather = weather;
    }
    public String getWeather() {
        return weather;
    }
}
