package models.time;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;


import models.Enums.Season;

public class GameTimeAndDate {

    public Time displayTime() {
        return null;
    }

    // Attributes
    private LocalDateTime gameStartTime;
    private float timeScaleMultiplier;
    private static Time currentTime;
    private Date date;
    private DayOfWeek currentDay = null;

    public static void setCurrentTime(Time time) {
        currentTime = time;
    }

    // Methods
    public void pauseTime() { /* Stop time progression */ }

    public void syncWithRealTime() { /* Real-time correlation */ }

    public Season getCurrentSeason() { /* Calculate from day count */ }

    public class GameTimeAndDate {
        // Add to existing class

        public boolean isNewDay() {
            return getCurrentHour() == 6 && !wasPreviousHour6;
        }
    }

    public static String showStatus() {

    }
}