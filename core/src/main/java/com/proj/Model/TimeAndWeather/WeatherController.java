package com.proj.Model.TimeAndWeather;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.proj.Model.TimeAndWeather.rainy.RainyWeather;
import com.proj.Model.TimeAndWeather.snowy.SnowyWeather;
import com.proj.Model.TimeAndWeather.time.ClockWidget;
import com.proj.Model.TimeAndWeather.time.TimeDisplayActor;

public class WeatherController {

    private Weather weather;
    private SnowyWeather snowRenderer;
    private RainyWeather rainRenderer;


    private Stage uistage;
    private ClockWidget clockWidget;
    private TimeDisplayActor timeDisplayActor;



    public WeatherController() {
        snowRenderer = new SnowyWeather();
        rainRenderer = new RainyWeather();
        weather = Weather.SUNNY;
    }

    public void update(Weather weather,float delta) {
        //  Weather weather;
        // Update sky color based on weather
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
            // Other weather types...
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
        }
    }

    public void resize(int width, int height) {

//        snowRenderer.resize(width, height);
        rainRenderer.resize(width, height);
    }

    public void dispose() {
        snowRenderer.dispose();
        rainRenderer.dispose();
    }

    public void changeWeather(Weather weather) {
        this.weather = weather;
    }

}
