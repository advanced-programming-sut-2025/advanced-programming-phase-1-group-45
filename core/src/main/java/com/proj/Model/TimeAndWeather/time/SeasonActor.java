package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SeasonActor extends Actor {
    private TextureRegion season;

    public SeasonActor(TextureRegion seasonTexture) {
        this.season = seasonTexture;
        setSize(season.getRegionWidth(), season.getRegionHeight());
        setOrigin(getWidth()/2, getHeight()/2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(season,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(), 0);
    }

    public void setSeason(TextureRegion season) {
        this.season = season;
    }
}
