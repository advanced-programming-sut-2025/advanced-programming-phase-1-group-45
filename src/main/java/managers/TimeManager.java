package managers;

import controllers.WeatherController;
import models.Enums.DayOfWeek;
import models.Events.*;
import models.Enums.Season;

public class TimeManager {
    private static TimeManager instance;
    private int hour = 9;
    private int day = 1;
    private Season season = Season.SPRING;
    private DayOfWeek dayOfWeek = DayOfWeek.Saturday;
    private int turnsTaken = 1;
    private int totalDaysPlayed = 0;

    public TimeManager() {
        instance = this;
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
        if (day % 4 == 0) {
            advanceDayOfWeek();
        }
        if (day == 28) {
            advanceSeason();
        }
    }

    private void advanceDayOfWeek(){
        dayOfWeek = dayOfWeek.next();
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void advanceSeason() {
        Season previousSeason = season;
        season = season.next();
        day = 1;
        GameEventBus.INSTANCE.post(new SeasonChangedEvent(previousSeason, season));
    }

    public int getTotalDaysPlayed() {
        return totalDaysPlayed;
    }

    public String getDateAndTimeString() {
        return String.format("Day %d , %02d:00 - %s",
                day, hour, season.toString());
    }

    public int getDay() {
        return day;
    }

    public Season getSeason() {
        return this.season;
    }

    public String getTimeString() {
        return String.format("%02d", hour);
    }
}
