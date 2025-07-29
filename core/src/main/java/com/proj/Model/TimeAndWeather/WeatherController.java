package com.proj.Model.TimeAndWeather;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.Model.TimeAndWeather.rainy.RainyWeather;
import com.proj.Model.TimeAndWeather.snowy.SnowyWeather;
import com.proj.Model.TimeAndWeather.stormy.StormyWeather;

public class WeatherController {

    private Weather weather;
    private SnowyWeather snowRenderer;
    private RainyWeather rainRenderer;
    private StormyWeather stormyRenderer;


    public WeatherController() {
        snowRenderer = new SnowyWeather();
        rainRenderer = new RainyWeather();
        stormyRenderer = new StormyWeather();
        weather = Weather.SUNNY;
    }

    public void update(Weather weather, float delta) {
        if (!this.weather.equals(weather)) {
            changeWeather(weather);
        }

        switch (weather) {
            case SNOWY:
                snowRenderer.update(delta);
                break;

            case RAINY:
                rainRenderer.update(delta);
                break;

            case STORMY:
                stormyRenderer.update(delta);
                break;
        }

    }

    public void render(SpriteBatch batch, Weather weather) {
        switch (weather) {
            case SNOWY:
                snowRenderer.render(batch);
                break;
            case RAINY:
                rainRenderer.render(batch);
                break;
            case STORMY:
                stormyRenderer.render(batch);
                break;
        }
    }

    public void resize(int width, int height) {
        switch (weather) {
            case SNOWY:
                snowRenderer.resize(width, height);
                break;
            case RAINY:
                rainRenderer.resize(width, height);
                break;

                case STORMY:
                    stormyRenderer.resize(width, height);
                    break;
        }
    }

    public void dispose() {
        snowRenderer.dispose();
        rainRenderer.dispose();
        stormyRenderer.dispose();
    }

    public void changeWeather(Weather weather) {
        this.weather = weather;
    }

}
