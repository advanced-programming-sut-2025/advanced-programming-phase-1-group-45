package models.Events;

import models.Enums.Season;

public class TurnAdvancedEvent(int hour, int day, Season season) {
    private int hour;
    private int day;
    private Season season;

    TurnAdvancedEvent(int hour, int day, Season season) {
        this.hour = hour;
        this.day = day;
        this.season = season;
    }
    public int getHour() {
        return hour;
    }
    public int getDay() {
        return day;
    }
    public Season getSeason() {
        return season;
    }
}
