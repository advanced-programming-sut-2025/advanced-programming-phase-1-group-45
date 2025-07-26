package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.proj.Model.GameAssetManager;
import com.proj.map.Season;

public class ClockWidget extends Group {
//    private final ClockHand clockHand;
    private final Time timer;
    private SeasonActor seasonActor;

    public ClockWidget(Time timer) {
        this.timer = timer;

        Texture clockTexture = GameAssetManager.getGameAssetManager().getClockBGTexture();
        TextureRegion clock = new TextureRegion(clockTexture,0,0, clockTexture.getWidth(), clockTexture.getHeight() );
        TextureRegion hand = new TextureRegion(GameAssetManager.getGameAssetManager().getClockHand());


        Image backgroundImg = new Image(clock);
        addActor(backgroundImg);

        TextureRegion seasonTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getSpringClock());
        seasonActor = new SeasonActor(seasonTexture);
        seasonActor.setPosition(backgroundImg.getWidth(), backgroundImg.getHeight());
        addActor(seasonActor);

//        this.clockHand = new ClockHand(hand);
//        // Center hand in clock
//        clockHand.setPosition(
//            (backgroundImg.getWidth() - clockHand.getWidth()) / 2,
//            (backgroundImg.getHeight() - clockHand.getHeight()) / 2
//        );
//        addActor(clockHand);

        setSize(backgroundImg.getWidth(), backgroundImg.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateSeason();
    }

    private Season lastSeason = Season.SPRING;

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
}
