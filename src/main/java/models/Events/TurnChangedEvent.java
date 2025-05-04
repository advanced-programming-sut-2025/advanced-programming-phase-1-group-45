package models.Events;

import models.Enums.Season;

public class TurnChangedEvent {
    private final int hour;
    private final int day;
    private final Season season;
    public TurnChangedEvent(int hour, int day, Season season){
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
