package controllers;

import com.google.common.eventbus.Subscribe;
import models.Enums.Season;
import models.Enums.Weather;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.Events.SeasonChangedEvent;
import models.Events.WeatherChangedEvent;
import managers.TimeManager;
import models.GameMap;

import java.util.Arrays;
import java.util.Random;

public class WeatherController {
    private static final WeatherController instance = new WeatherController();
    private Weather currentWeather = Weather.SUNNY;
    private Weather forecastWeather;

    public static WeatherController getInstance() {
        return instance;
    }

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(String weather) {
        if (weather.equalsIgnoreCase("SNOWY")) {
            currentWeather = Weather.SNOWY;
        } else if (weather.equalsIgnoreCase("Rainy")) {
            currentWeather = Weather.RAINY;
        } else if(weather.equalsIgnoreCase("Stormy")) {
            currentWeather = Weather.STORMY;
        } else if (weather.equalsIgnoreCase("Sunny")) {
            currentWeather = Weather.SUNNY;
        } else {
            System.out.println("invalid weather");
        }
    }

    public WeatherController() {
        GameEventBus.INSTANCE.register(this);
        // Initialize weather
        Season firstSeason = TimeManager.getInstance().getSeason();
        randomlyChangeWeatherForTomorrow(firstSeason);
    }
//
//    @Subscribe
//    public void newWeatherForNewSeason(SeasonChangedEvent event) {
//        randomlyChangeWeatherForTomorrow(event.newSeason());
//        System.out.println(currentWeather.toString());
//    }

    @Subscribe
    public void onDayChanged(DayChangedEvent event) {
        Weather previousWeather = currentWeather;
        currentWeather = forecastWeather;
        if (TimeManager.getInstance().getDay() % 27 == 0) {
            randomlyChangeWeatherForTomorrow(event.season().next());
        } else {
            randomlyChangeWeatherForTomorrow(event.season());
        }
        GameEventBus.INSTANCE.post(new WeatherChangedEvent(currentWeather, previousWeather,
                event.season()));
        //  System.out.println(currentWeather.toString() + " " + event.season().toString());
    }

    private void randomlyChangeWeatherForTomorrow(Season season) {
        Weather[] weathers = season.getWeathers();
        forecastWeather = weathers[new Random().nextInt(weathers.length)];
    }

    public Weather getForecastWeather() {
        return forecastWeather;
    }

//    public void thor(WeatherChangedEvent event) {
//        if (event.newWeather().equals(Weather.STORMY))
//        }
//    }
}
