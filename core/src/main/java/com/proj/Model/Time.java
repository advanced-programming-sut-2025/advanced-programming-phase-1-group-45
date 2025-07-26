package com.proj.Model;

import com.proj.Map.Season;

public class Time {
    private int day;
    private int hour;
    private Season season;
    private Weather weather;
    private DayOfWeek dayOfWeek;
    private int dayPassed;

    //initialize
    Time() {
        day = 1;
        hour = 9;
        dayOfWeek = DayOfWeek.Saturday;
        season = Season.SPRING;
        weather = Weather.SUNNY;
    }

    public void advanceHour(int hour) {
        this.hour += hour;
        if (this.hour >= 22) {
            this.hour = 9;
            advanceDay(1);
        }
    }

    public void advanceDay(int day) {
        this.day += day;
        this.dayOfWeek = DayOfWeek.values()[dayOfWeek.ordinal() + day];
        if (this.day >= 28) {
            this.day = 1;
            advanceSeason(Season.values()[season.ordinal() + 1]);
        }
    }

    public void advanceSeason(Season season) {
        this.season = season;
    }

    public int getDay() {
        return day;
    }

}
