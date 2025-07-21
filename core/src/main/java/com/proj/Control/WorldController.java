package com.proj.Control;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.Model.Weather;
import com.proj.Model.WeatherRender;

public class WorldController {

    private WeatherRender weatherRender;

    public WorldController() {
        this.weatherRender = new WeatherRender();
    }
    public void update(float delta) {
        weatherRender.update(Weather.SNOWY,delta );
    }
    public void render(SpriteBatch batch, Weather weather) {
        weatherRender.render(batch, weather);
    }

    public void resize(int width, int height) {
        weatherRender.resize(width, height);
    }
    public void dispose() {
        weatherRender.dispose();
    }

}
