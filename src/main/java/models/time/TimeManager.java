package models.time;

import models.Events.*;
import models.Enums.Season;

public class TimeManager {
    private static final TimeManager instance = new TimeManager();
    private int hour = 9;
    private int day = 1;
    private Season season = Season.SPRING;
    private int turnsTaken;
    private int totalDaysPlayed = 0;
    private TimeManager() {
        this.season = Season.SPRING;
    }
    public static TimeManager getInstance() {
        return instance;
    }

    public void advanceTurn() {
        turnsTaken++;
        GameEventBus.INSTANCE.post(new TurnAdvancedEvent(hour, day, season));
        if (turnsTaken == 3) {
            advanceHour();
            turnsTaken = 0;
        }
    }
    public void advanceHour(){
         hour ++;
        GameEventBus.INSTANCE.post(new HourAdvancedEvent(hour, day, season));
        if(hour == 22) {
             advanceDay();
             hour = 9;
         }
    }

    private void advanceDay() {
        day++;
        totalDaysPlayed++;
        GameEventBus.INSTANCE.post(new DayAdvancedEvent(day, season));
        if (day > 28) {
            advanceSeason();
        }
    }

    private void advanceSeason() {
        Season previousSeason = season;
        season = season.next();
        day = 1;
        GameEventBus.INSTANCE.post(new SeasonChangedEvent(previousSeason, season));
    }

    public String getTimeString() {
        return String.format("Day %d , %02d:00 - %s", day, hour, season.toString());
    }
}
