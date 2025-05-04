package models.Events;

import models.Enums.Season;
import models.Enums.Weather;

public class WeatherChangedEvent {
    private final Weather newWeather;
    private final Weather oldWeather;
    private final Season season;
    public WeatherChangedEvent(Weather newWeather, Weather oldWeather, Season season){
        this.newWeather = newWeather;
        this.oldWeather = oldWeather;
        this.season = season;
    }
    public Weather getNewWeather() {
        return newWeather;
    }
    public Weather getOldWeather() {
        return oldWeather;
    }
    public Season getSeason() {
        return season;
    }
}
