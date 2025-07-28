package com.proj.Model.TimeAndWeather;

import com.proj.map.Season;

public class TimeAndDate {
    private int day = 1;
    private int month = 1; // 1-12
    private int year = 1;
    private int hour = 8; // 0-23
    private int minute = 0; // 0-59
    private Season currentSeason = Season.SPRING;
    private float timeSpeed = 1.0f; // ضریب سرعت زمان

    private float accumulatedTime = 0;


    public void update(float delta) {
        accumulatedTime += delta * timeSpeed;

        // هر 10 ثانیه واقعی = 1 ساعت بازی
        if(accumulatedTime >= 10) {
            accumulatedTime = 0;
            minute += 10;

            if(minute >= 60) {
                minute = 0;
                hour++;

                if(hour >= 24) {
                    hour = 0;
                    advanceDay();
                }
            }
        }
    }

    private void advanceDay() {
        day++;

        // محاسبه فصل بر اساس ماه
        if(day > 28) { // هر ماه 28 روز
            day = 1;
            month++;

            if(month > 12) {
                month = 1;
                year++;
            }

            // بروزرسانی فصل
            if(month >= 3 && month <= 5) currentSeason = Season.SPRING;
            else if(month >= 6 && month <= 8) currentSeason = Season.SUMMER;
            else if(month >= 9 && month <= 11) currentSeason = Season.FALL;
            else currentSeason = Season.WINTER;
        }
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }

    public String getFormattedDate() {
        String monthName = "";
        switch(month) {
            case 1: monthName = "فروردین"; break;
            case 2: monthName = "اردیبهشت"; break;
            case 3: monthName = "خرداد"; break;
            case 4: monthName = "تیر"; break;
            case 5: monthName = "مرداد"; break;
            case 6: monthName = "شهریور"; break;
            case 7: monthName = "مهر"; break;
            case 8: monthName = "آبان"; break;
            case 9: monthName = "آذر"; break;
            case 10: monthName = "دی"; break;
            case 11: monthName = "بهمن"; break;
            case 12: monthName = "اسفند"; break;
        }
        return "روز " + day + " " + monthName + " سال " + year;
    }

    public Season getSeason() {
        return currentSeason;
    }

    public boolean isNight() {
        return hour < 6 || hour >= 20;
    }

    public float getDayProgress() {
        // پیشرفت روز (0.0-1.0)
        return ((hour * 60 + minute) / (24f * 60f));
    }

    // ... سایر متدها

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setCurrentSeason(Season currentSeason) {
        this.currentSeason = currentSeason;
    }

    public void setTimeSpeed(float timeSpeed) {
        this.timeSpeed = timeSpeed;
    }

    public void setAccumulatedTime(float accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public int getDay() {
        return day;
    }

    public float getTimeSpeed() {
        return timeSpeed;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public float getAccumulatedTime() {
        return accumulatedTime;
    }
}
