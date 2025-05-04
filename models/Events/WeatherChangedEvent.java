package models.Events;

import models.Enums.Season;
import models.Enums.Weather;

public record WeatherChangedEvent {
    private Weather newWeather;
    private Weather oldWeather;
    private Season season;

    WeatherChangedEvent(Weather newWeather, Weather oldWeather, Season season) {
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
