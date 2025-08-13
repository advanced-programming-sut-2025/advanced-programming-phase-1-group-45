package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.math.MathUtils;
import com.proj.Main;
import com.proj.Model.TimeAndWeather.DayOfWeek;
import com.proj.Model.TimeAndWeather.Weather;
import com.proj.map.Season;
import com.proj.network.client.GameEventListener;
import com.proj.network.event.GameEvent;
import com.proj.network.event.NetworkEvent;
import com.proj.network.message.JsonParser;
import org.json.JSONObject;

public class GameTime {
    private int day;
    private int hour;
    private int minute;
    private Season season;
    private Weather weather;
    private DayOfWeek dayOfWeek;
    private int dayPassed;
    private float frame;
    private boolean isPaused;
    private boolean isNight = false;
    boolean dayChanged = false;
    boolean isNewDay = true;

    public GameTime() {
        day = 1;
        hour = 9;
        minute = 0;
        dayOfWeek = DayOfWeek.Saturday;
        season = Season.SPRING;
        weather = Weather.SUNNY;
        frame = 0;
        dayPassed = 0;
        isNight = hour >= 18;
    }

    public void update(float delta, boolean isPaused) {
        this.isPaused = isPaused;
        if (!isPaused) {
            frame++;
            // frame = 1/60 sec
            // 60 frame = 1 sec
            //30 sec = 1 hour (*120)
            //30*13 = 390 sec = 1 day
            // 6 min , 30 sec = 1 day
            if (frame >= 30) {
                frame = 0;
                minute++;
            }
            if (minute >= 60) {
                minute = 0;
                advanceHour(1);
                if (dayChanged) {
                    dayChanged = false;
                    weather = generateRandomWeather();
                    System.err.println("weather = " + weather.toString());
                }
                isNight = (hour > 18 || (hour == 18 && minute > 0));
            }
        }
    }

    public void advanceHour(int hours) {
        this.hour += hours;
        if (this.hour >= 22) {
            int extraHours = this.hour - 22;
            this.hour = 9 + extraHours;
            advanceDay(1);
        }
    }

    public void advanceDay(int days) {
        isNewDay = true;
        for (int i = 0; i < days; i++) {
            day++;
            dayPassed++;

            // Update day of week with wrap-around
            dayOfWeek = DayOfWeek.values()[(dayOfWeek.ordinal() + 1) % 7];

            if (day > 28) {
                day = 1;
                // Update season with wrap-around
                int nextOrdinal = (season.ordinal() + 1) % Season.values().length;
                season = Season.values()[nextOrdinal];
                System.err.println("season changed to = " + season.toString());
            }
        }
    }

    public boolean isNewDay() {
        boolean preIsNewDay = isNewDay;
        isNewDay = false;
        return preIsNewDay;
    }

    private Weather generateRandomWeather() {
        Weather[] weathers;
        switch (season) {
            case SPRING:
                weathers = new Weather[]{Weather.SUNNY, Weather.RAINY,
                    Weather.SUNNY, Weather.STORMY, Weather.RAINY};
                break;
            case SUMMER:
                weathers = new Weather[]{Weather.SUNNY, Weather.RAINY,
                    Weather.SUNNY, Weather.STORMY, Weather.SUNNY};
                break;
            case FALL:
                weathers = new Weather[]{Weather.RAINY, Weather.STORMY,
                    Weather.SUNNY, Weather.RAINY, Weather.STORMY};
                break;
            case WINTER:
                weathers = new Weather[]{Weather.SNOWY, Weather.STORMY,
                    Weather.SNOWY, Weather.SUNNY, Weather.SNOWY};
                break;
            default:
                weathers = new Weather[]{Weather.SUNNY};
        }
        return weathers[MathUtils.random(1, weathers.length - 1)];
    }

    public boolean isNight() {
        return isNight;
    }

    public int getDay() { return day; }
    public Season getSeason() { return season; }
    public int getHour() { return hour; }
    public Weather getWeather() { return weather; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public int getDayPassed() { return dayPassed; }
    public int getMinute() { return minute; }
    public boolean isPaused() { return isPaused; }

    public String getTimeForDisplay() {
        StringBuilder sb = new StringBuilder();
        if (hour <= 12) {
            sb.append(hour);
        } else {
            sb.append(hour - 12);
        }
        sb.append(":").append(String.format("%02d", minute));
        if (hour <= 12) {
            sb.append(" am");
        } else {
            sb.append(" pm");
        }
        return sb.toString();
    }

    public String getDateForDisplay() {
        StringBuilder sb = new StringBuilder();
        switch (dayOfWeek) {
            case Saturday:
                sb.append("Sat.");
                break;
            case Sunday:
                sb.append("Sun.");
                break;
            case Monday:
                sb.append("Mon.");
                break;
            case Tuesday:
                sb.append("Tue.");
                break;
            case Wednesday:
                sb.append("Wed.");
                break;
            case Thursday:
                sb.append("Thu.");
                break;
            case Friday:
                sb.append("Fri.");
                break;
        }
        sb.append(day);
        return sb.toString();
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setDayPassed(int dayPassed) {
        this.dayPassed = dayPassed;
    }

    public void setFrame(float frame) {
        this.frame = frame;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    public void setDayChanged(boolean dayChanged) {
        this.dayChanged = dayChanged;
    }

    public void setNewDay(boolean newDay) {
        isNewDay = newDay;
    }
}

