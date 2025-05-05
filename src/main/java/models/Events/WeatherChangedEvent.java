package models.Events;

import models.Enums.Season;
import models.Enums.Weather;

public record WeatherChangedEvent(Weather newWeather, Weather previousWeather,
                                  Season season) {
}
