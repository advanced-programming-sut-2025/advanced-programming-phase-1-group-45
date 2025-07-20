package com.proj.Model;

public enum Weather {
    SUNNY("Sunny"),
    SNOWY("Snowy"),
    RAINY("Rainy"),
    WINDY("Windy");
    private String weather;
    Weather(String weather) {
        this.weather = weather;
    }
    public String getWeather() {
        return weather;
    }
}
