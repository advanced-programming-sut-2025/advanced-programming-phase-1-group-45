package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.proj.map.FarmInOutPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    public static final int[] AVATAR_IDS = {0, 1, 2, 3};

    private Skin skin;
    private TextureRegion snowflake;
    private BitmapFont smallFont;
    private BitmapFont tinyFont;
    private BitmapFont spriteFont;

    private Texture clockTexture;
    private Texture clockHand;
    private Texture fallClock;
    private Texture springClock;
    private Texture summerClock;
    private Texture winterClock;

    private List<FarmInOutPoint> exitPointList = new ArrayList<>();



    private GameAssetManager() {
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        snowflake = new TextureRegion(new Texture("snowflake.png"));

        //clock
        clockTexture = new Texture("clock/clockmy.png");
        clockHand = new Texture("clock/clock_hand.png");
        fallClock = new Texture("clock/fall.png");
        springClock = new Texture("clock/spring.png");
        summerClock = new Texture("clock/summer.png");
        winterClock = new Texture("clock/winter.png");

        smallFont = new BitmapFont(Gdx.files.internal("smallFont/exo-small.fnt"));
        loadFarmExitPoints();
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null)
            gameAssetManager = new GameAssetManager();
        return gameAssetManager;
    }

    public Skin getSkin() {
        return skin;
    }

    public TextureRegion getSnowflake() {
        return snowflake;
    }

    public BitmapFont getTinyFont() {
        return tinyFont;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public BitmapFont getSpriteFont() {
        return spriteFont;
    }

    public Texture getClockHand() {
        return clockHand;
    }

    public Texture getFallClock() {
        return fallClock;
    }

    public Texture getSpringClock() {
        return springClock;
    }

    public Texture getClockBGTexture() {
        return clockTexture;
    }

    public Texture getSummerClock() {
        return summerClock;
    }

    public Texture getWinterClock() {
        return winterClock;
    }


    private void loadFarmExitPoints() {
        FarmInOutPoint standard = new FarmInOutPoint("Standard", 1);
        standard.addFarmExitPoint(new Point(40, 65), 5);
        standard.addFarmExitPoint(new Point(41, 65), 5);

        standard.addFarmEnterPoint(new Point(40, 64) , 5);

        standard.addFarmExitPoint(new Point(41, 3), 6);
        standard.addFarmExitPoint(new Point(41, 3), 6);
        standard.addFarmEnterPoint(new Point(41, 4) , 6);


        standard.addFarmExitPoint(new Point(80, 48 ), 2);
        standard.addFarmEnterPoint(new Point(79, 48) , 2);

        exitPointList.add(standard);

        FarmInOutPoint BackWoods = new FarmInOutPoint("BackWoods", 5);
        BackWoods.addFarmExitPoint(new Point(14, 0), 1);
        BackWoods.addFarmExitPoint(new Point(13, 0), 1);
        BackWoods.addFarmExitPoint(new Point(15, 0), 1);
        BackWoods.addFarmEnterPoint(new Point(14, 2), 1);

        BackWoods.addFarmExitPoint(new Point(48, 24), 4);
        BackWoods.addFarmEnterPoint(new Point(47, 24), 4);
        exitPointList.add(BackWoods);

        FarmInOutPoint town = new FarmInOutPoint("Town" ,3);
        town.addFarmExitPoint(new Point(0, 55), 2);
        town.addFarmExitPoint(new Point(0, 56), 2);
        town.addFarmEnterPoint(new Point(1, 55), 2);

        town.addFarmExitPoint(new Point(79, 110), 4);
        town.addFarmExitPoint(new Point(80, 110), 4);
        town.addFarmExitPoint(new Point(81, 110), 4);
        town.addFarmExitPoint(new Point(82, 110), 4);
        town.addFarmExitPoint(new Point(83, 110), 4);
        town.addFarmEnterPoint(new Point(80, 109), 4);

        town.addFarmExitPoint(new Point(54, 0), 7);
        town.addFarmExitPoint(new Point(53, 0), 7);
        town.addFarmExitPoint(new Point(55, 0), 7);

        town.addFarmEnterPoint(new Point(64, 1), 7);
        exitPointList.add(town);

        FarmInOutPoint forest = new FarmInOutPoint("Forest", 6);
        forest.addFarmExitPoint(new Point(67, 120) , 1);
        forest.addFarmExitPoint(new Point(68, 120), 1);
        forest.addFarmExitPoint(new Point(69, 120), 1);
        forest.addFarmExitPoint(new Point(70, 120), 1);
        forest.addFarmExitPoint(new Point(71, 120), 1);
        forest.addFarmExitPoint(new Point(72, 120), 1);
        forest.addFarmEnterPoint(new Point(70, 119), 1);


        forest.addFarmExitPoint(new Point(120, 95), 3);
        forest.addFarmEnterPoint(new Point(119, 95), 3);
        exitPointList.add(forest);

        FarmInOutPoint busStop = new FarmInOutPoint("BusStop", 2);
        busStop.addFarmExitPoint(new Point(45, 6), 3);
        busStop.addFarmExitPoint(new Point(45, 6), 3);

        busStop.addFarmEnterPoint(new Point(44, 6), 3);


        busStop.addFarmExitPoint(new Point(11, 7), 1);
        busStop.addFarmEnterPoint(new Point(12, 7), 1);

        exitPointList.add(busStop);

        FarmInOutPoint mountain = new FarmInOutPoint("Mountain", 4);
        mountain.addFarmExitPoint(new Point(0, 28), 5);
        mountain.addFarmEnterPoint(new Point(1,27),5 );

        mountain.addFarmExitPoint(new Point(15, 0), 3);
        mountain.addFarmExitPoint(new Point(16, 0), 3);
        mountain.addFarmEnterPoint(new Point(15, 1), 3);

        exitPointList.add(mountain);
    }

    public List<FarmInOutPoint> getExitPointList() {
        return exitPointList;
    }

}


