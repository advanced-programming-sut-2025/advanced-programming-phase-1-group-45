package controllers;

import com.google.common.eventbus.Subscribe;
import models.Enums.Season;
import models.Enums.Weather;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.Events.SeasonChangedEvent;
import models.Events.WeatherChangedEvent;

import java.util.Random;

public class WeatherController {
    private final Random random = new Random();
    private Weather currentWeather;

    public WeatherController() {
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void onSeasonChanged(SeasonChangedEvent event) {
        randomlyChangeWeather(event.newSeason());
        System.out.println(currentWeather.toString());
    }

    @Subscribe
    public void onDayChanged(DayChangedEvent event) {
        Weather previousWeather = currentWeather;
        randomlyChangeWeather(event.season());
        GameEventBus.INSTANCE.post(new WeatherChangedEvent(currentWeather, previousWeather,
                event.season()));
    }

    private void randomlyChangeWeather(Season season) {
        Weather[] weathers = season.getWeathers();
        currentWeather = weathers[random.nextInt(weathers.length)];
    }
}
