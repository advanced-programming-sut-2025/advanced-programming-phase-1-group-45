package managers.time;

import controllers.WeatherController;
import models.Events.*;
import models.Enums.Season;

public class TimeManager {
    private static final TimeManager instance = new TimeManager();
    private int hour = 9;
    private int day = 1;
    private Season season = Season.SPRING;
    private int turnsTaken = 1;
    private int totalDaysPlayed = 0;
    private static final WeatherController weatherController = new WeatherController();

    private TimeManager() {
        this.season = Season.SPRING;
    }

    public static TimeManager getInstance() {
        return instance;
    }

    public void nextTurn() {
        turnsTaken++;
        GameEventBus.INSTANCE.post(new TurnChangedEvent(hour, day, season));
        if (turnsTaken == 3) {
            advanceHour();
            turnsTaken = 0;
        }
    }

    public void advanceHour() {
        hour++;
        GameEventBus.INSTANCE.post(new HourAdvancedEvent(hour, day, season));
        if (hour == 22) {
            advanceDay();
            hour = 9;
        }
    }

    public void advanceDay() {
        day++;
        totalDaysPlayed++;
        GameEventBus.INSTANCE.post(new DayChangedEvent(day, season));
        if (day == 28) {
            advanceSeason();
        }
    }

    public void advanceSeason() {
        Season previousSeason = season;
        season = season.next();
        day = 1;
        GameEventBus.INSTANCE.post(new SeasonChangedEvent(hour, day, previousSeason, season));
    }

    public int getTotalDaysPlayed() {
        return totalDaysPlayed;
    }

    public String getTimeString() {
        return String.format("Day %d , %02d:00 - %s",
                day, hour, season.toString());
    }

    public Season getSeason() {
        return this.season;
    }
}
