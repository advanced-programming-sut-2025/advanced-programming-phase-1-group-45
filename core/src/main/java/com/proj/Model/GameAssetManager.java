package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.TimeAndWeather.Weather;
import com.proj.Model.mapObjects.ForagingItem;
import com.proj.Model.mapObjects.NaturalResource;
import com.proj.map.FarmInOutPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<String, TextureRegion> weatherClocks;

    private Texture thunder;
    private Texture lanternLight;


    private List<FarmInOutPoint> exitPointList = new ArrayList<>();
    private HashMap<String, Texture> foragingItem = new HashMap<>();
    private ForagingLoader foragingLoader;
    private HashMap<String, NaturalResource> resources = new HashMap<>();


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
        weatherClocks = new HashMap<>();
        weatherClocks.put("sunny", new TextureRegion(new Texture("clock/sunny.png")));
        weatherClocks.put("rainy", new TextureRegion(new Texture("clock/rainy.png")));
        weatherClocks.put("snowy", new TextureRegion(new Texture("clock/snowy.png")));
        weatherClocks.put("stormy", new TextureRegion(new Texture("clock/stormy.png")));
        thunder = new Texture("assets/thunderStorm.png");
        lanternLight = new Texture("assets/lantern.png");
        smallFont = new BitmapFont(Gdx.files.internal("smallFont/exo-small.fnt"));
        loadFarmExitPoints();
        loadForagingItems();
        loadResources();
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null)
            gameAssetManager = new GameAssetManager();
        return gameAssetManager;
    }

    public Skin getSkin() {
        foragingLoader = new ForagingLoader();
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

    public TextureRegion getWeatherTexture(Weather weather) {
        return weatherClocks.get(weather.toString().toLowerCase());
    }

    public Texture getThunder() {
        return thunder;
    }

    public Texture getLanternLight() {
        return lanternLight;
    }

    public Array<ForagingItem> getForagingCrops() {
        return foragingLoader.getForagingCrops();
    }

    private void loadFarmExitPoints() {
        FarmInOutPoint standard = new FarmInOutPoint("Standard", 1);
        standard.addFarmExitPoint(new Point(40, 65), 5);
        standard.addFarmExitPoint(new Point(41, 65), 5);

        standard.addFarmEnterPoint(new Point(40, 64), 5);

        standard.addFarmExitPoint(new Point(41, 3), 6);
        standard.addFarmExitPoint(new Point(41, 3), 6);
        standard.addFarmEnterPoint(new Point(41, 4), 6);


        standard.addFarmExitPoint(new Point(80, 48), 2);
        standard.addFarmEnterPoint(new Point(79, 48), 2);

        exitPointList.add(standard);

        FarmInOutPoint backWoods = new FarmInOutPoint("BackWoods", 5);
        backWoods.addFarmExitPoint(new Point(14, 0), 1);
        backWoods.addFarmExitPoint(new Point(13, 0), 1);
        backWoods.addFarmExitPoint(new Point(15, 0), 1);
        backWoods.addFarmEnterPoint(new Point(14, 2), 1);

        backWoods.addFarmExitPoint(new Point(50, 27), 4);
        backWoods.addFarmExitPoint(new Point(50, 26), 4);
        backWoods.addFarmExitPoint(new Point(50, 25), 4);
        backWoods.addFarmExitPoint(new Point(50, 24), 4);
        backWoods.addFarmEnterPoint(new Point(49, 26), 4);
        exitPointList.add(backWoods);

        FarmInOutPoint town = new FarmInOutPoint("Town", 3);
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

        town.addFarmEnterPoint(new Point(54, 1), 7);

        town.addFarmExitPoint(new Point(0, 20), 6);
        town.addFarmExitPoint(new Point(0, 19), 6);
        town.addFarmExitPoint(new Point(0, 18), 6);
        town.addFarmExitPoint(new Point(0, 17), 6);

        town.addFarmEnterPoint(new Point(1, 19), 6);

        exitPointList.add(town);

        FarmInOutPoint forest = new FarmInOutPoint("Forest", 6);
        forest.addFarmExitPoint(new Point(67, 120), 1);
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
        mountain.addFarmEnterPoint(new Point(1, 28), 5);

        mountain.addFarmExitPoint(new Point(15, 0), 3);
        mountain.addFarmExitPoint(new Point(16, 0), 3);
        mountain.addFarmEnterPoint(new Point(15, 1), 3);

        exitPointList.add(mountain);

        FarmInOutPoint beach = new FarmInOutPoint("Beach", 7);
        beach.addFarmExitPoint(new Point(38, 49), 3);
        beach.addFarmExitPoint(new Point(39, 49), 3);
        beach.addFarmEnterPoint(new Point(38, 45), 3);

        exitPointList.add(beach);
    }

    private void loadForagingItems() {
        foragingItem.put("commonmushroom", new Texture("assets/foraging/Common_Mushroom.png"));
        foragingItem.put("daffodil", new Texture("assets/foraging/Daffodil.png"));
        foragingItem.put("dandelion", new Texture("assets/foraging/Dandelion.png"));
        foragingItem.put("leek", new Texture("assets/foraging/Leek.png"));
        foragingItem.put("morel", new Texture("assets/foraging/Morel.png"));
        foragingItem.put("salmonberry", new Texture("assets/foraging/Salmonberry.png"));
        foragingItem.put("springonion", new Texture("assets/foraging/Spring_Onion.png"));
        foragingItem.put("wildhorseradish", new Texture("assets/foraging/Wild_Horseradish.png"));
        foragingItem.put("fiddleheadfern", new Texture("assets/foraging/Fiddlehead_Fern.png"));
        foragingItem.put("grape", new Texture("assets/foraging/Grape.png"));
        foragingItem.put("redmushroom", new Texture("assets/foraging/Red_Mushroom.png"));
        foragingItem.put("spiceberry", new Texture("assets/foraging/Spice_Berry.png"));
        foragingItem.put("sweetpea", new Texture("assets/foraging/Sweet_Pea.png"));
        foragingItem.put("blackberry", new Texture("assets/foraging/Blackberry.png"));
        foragingItem.put("chanterelle", new Texture("assets/foraging/Chanterelle.png"));
        foragingItem.put("hazelnut", new Texture("assets/foraging/Hazelnut.png"));
        foragingItem.put("purplemushroom", new Texture("assets/foraging/Purple_Mushroom.png"));
        foragingItem.put("wildplum", new Texture("assets/foraging/Wild_Plum.png"));
        foragingItem.put("crocus", new Texture("assets/foraging/Crocus.png"));
        foragingItem.put("crystalfruit", new Texture("assets/foraging/Crystal_Fruit.png"));
        foragingItem.put("holly", new Texture("assets/foraging/Holly.png"));
        foragingItem.put("snowyam", new Texture("assets/foraging/Snow_Yam.png"));
        foragingItem.put("winterroot", new Texture("assets/foraging/Winter_Root.png"));
    }

    private void loadResources() {
        TextureRegion fiber = new TextureRegion(new Texture("assets/resources/fiber.png"));
        TextureRegion stone = new TextureRegion(new Texture("assets/resources/stone.png"));
        TextureRegion wood = new TextureRegion(new Texture("assets/resources/wood.png"));
        resources.put("fiber", new NaturalResource("naturalResource", "Fiber", fiber, true, 7));
        resources.put("stone", new NaturalResource("naturalResource", "Stone", stone, true, 7));
        resources.put("wood", new NaturalResource("naturalResource", "Wood", wood, true, 7));
    }

    public List<FarmInOutPoint> getExitPointList() {
        return exitPointList;
    }

    public Texture getForagingTexture(String forageName) {
        if(!foragingItem.containsKey(forageName)) {
            return null;
        }
        return foragingItem.get(forageName);
    }

    public HashMap<String , NaturalResource> getNaturalResourceList() {
        return resources;
    }
}


