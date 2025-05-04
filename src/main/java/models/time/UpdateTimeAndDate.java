package models.time;

import java.sql.Time;

public interface UpdateTimeAndDate {
    abstract void initialize();

    abstract Time updateTime();
}
