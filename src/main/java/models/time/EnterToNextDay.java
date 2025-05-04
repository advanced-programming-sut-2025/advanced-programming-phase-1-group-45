package models.time;

import java.sql.Time;

public class EnterToNextDay {
    public void sleep() {
    }

    public void updateTimeToNextDay() {
        GameTimeAndDate.setCurrentTime(Time.valueOf("09:00:00"));
    }
}
