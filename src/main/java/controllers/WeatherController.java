package controllers;

import com.google.common.eventbus.Subscribe;
import models.Enums.Season;
import models.Enums.Weather;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.Events.SeasonChangedEvent;
import models.Events.WeatherChangedEvent;
import managers.TimeManager;

import java.util.Random;

public class WeatherController {
    private static final WeatherController instance = new WeatherController();
    private Weather currentWeather;

    public static WeatherController getInstance() {
        return instance;
    }

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public WeatherController() {
        GameEventBus.INSTANCE.register(this);
        // Initialize weather
        Season firstSeason = TimeManager.getInstance().getSeason();
        randomlyChangeWeather(firstSeason);
        //    System.out.println(currentWeather.toString());
    }

    @Subscribe
    public void newWeatherForNewSeason(SeasonChangedEvent event) {
        randomlyChangeWeather(event.newSeason());
        System.out.println(currentWeather.toString());
    }

    @Subscribe
    public void onDayChanged(DayChangedEvent event) {
        Weather previousWeather = currentWeather;
        randomlyChangeWeather(event.season());
        GameEventBus.INSTANCE.post(new WeatherChangedEvent(currentWeather, previousWeather,
                event.season()));
        //  System.out.println(currentWeather.toString() + " " + event.season().toString());

    }

    private void randomlyChangeWeather(Season season) {
        Weather[] weathers = season.getWeathers();
        currentWeather = weathers[new Random().nextInt(weathers.length)];
    }
}
