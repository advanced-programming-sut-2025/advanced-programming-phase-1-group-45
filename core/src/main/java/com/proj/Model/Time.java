package com.proj.Model;

import com.proj.map.Season;

public class Time {
    private int day;
    private int hour;
    private Season season;
    private Weather weather;

    //initialize
    Time() {
        day = 1;
        hour = 9;
        season = Season.SPRING;
        weather = Weather.SUNNY;
    }

}
