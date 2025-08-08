package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.proj.Model.GameAssetManager;
import com.proj.Model.TimeAndWeather.Weather;
import com.proj.Map.Season;

public class ClockWidget extends Group {
    private final Time timer;
    private SeasonActor seasonActor;
    private WeatherActor weatherActor;

    public ClockWidget(Time timer) {
        this.timer = timer;
        Texture clockTexture = GameAssetManager.getGameAssetManager().getClockBGTexture();
        TextureRegion clock = new TextureRegion(clockTexture,0,0, clockTexture.getWidth(), clockTexture.getHeight() );
        Image backgroundImg = new Image(clock);
        addActor(backgroundImg);
        TextureRegion seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getSpringClock());
        seasonActor = new SeasonActor(seasonTexture);
        seasonActor.setPosition(backgroundImg.getWidth() - seasonActor.getWidth() / 2 - 36,
            backgroundImg.getHeight() - seasonActor.getHeight() / 2 - 54);
        TextureRegion weatherTexture = GameAssetManager.getGameAssetManager().getWeatherTexture(Weather.SUNNY);
        weatherActor = new WeatherActor(weatherTexture);
        weatherActor.setPosition(backgroundImg.getWidth() - weatherActor.getWidth() / 2 - 104,
            backgroundImg.getHeight() - weatherActor.getHeight() / 2 - 56);
        addActor(seasonActor);
        addActor(weatherActor);
        setSize(backgroundImg.getWidth(), backgroundImg.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateSeason();
        updateWeather();
    }

    private Season lastSeason = Season.SPRING;
    private Weather lastWeather = Weather.SUNNY;

    private void updateSeason() {
        Season season1 = timer.getSeason();
        if (season1 != lastSeason) {
            lastSeason = season1;
            TextureRegion seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getSpringClock());
            switch (season1) {
                case SPRING:
                    seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getSpringClock());
                    break;
                case SUMMER:
                    seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getSummerClock());
                    break;
                case WINTER:
                    seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getWinterClock());
                    break;
                case FALL:
                    seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getFallClock());
                    break;

            }
            seasonActor.setSeason(seasonTexture);
        }
    }

    private void updateWeather() {
        Weather weather1 = timer.getWeather();
        if (weather1 != lastWeather) {
            lastWeather = weather1;
            TextureRegion weatherTexture = GameAssetManager.getGameAssetManager().getWeatherTexture(weather1);
            weatherActor.setWeather(weatherTexture);
        }
    }
}
