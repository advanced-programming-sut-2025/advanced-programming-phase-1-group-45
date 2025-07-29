package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class WeatherActor extends Actor {
    private TextureRegion weather;

    public WeatherActor(TextureRegion weatherTexture) {
        this.weather = weatherTexture;
        setSize(weather.getRegionWidth(), weather.getRegionHeight());
        setOrigin(getWidth()/2, getHeight()/2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(weather,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(), 0);
    }

    public void setWeather(TextureRegion weather) {
        this.weather = weather;
    }
}
