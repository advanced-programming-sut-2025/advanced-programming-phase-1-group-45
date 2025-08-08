package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.TimeAndWeather.Weather;
import com.proj.Model.inventoryItems.ForagingItem;
import com.proj.Model.inventoryItems.ResourceItem;
import com.proj.Model.inventoryItems.seeds.SeedRegistry;
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
    private Skin stardewSkin;
    private Texture spaceImageTexture;


    private List<FarmInOutPoint> exitPointList = new ArrayList<>();
    private HashMap<String, Texture> foragingItem = new HashMap<>();
    private ForagingLoader foragingLoader;
    private HashMap<String, ResourceItem> resources = new HashMap<>();

    private HashMap<String, TextureRegion> foodTextures;
    private TextureRegion refrigeratorTexture;
    private TextureRegion buffIconTexture;
    private TextureRegion eatingEffectTexture;
    private TextureRegion defaultIngredientTexture;


    private GameAssetManager() {
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        snowflake = new TextureRegion(new Texture("snowflake.png"));
        stardewSkin = new Skin(Gdx.files.internal("assets/LibGdx-Skin-main/NzSkin.json"));

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
        spaceImageTexture = new Texture(Gdx.files.internal("NPCTable/npc_table.png"));
        loadFoodTextures();
        loadObjectTextures();
        loadEffectTextures();

        defaultIngredientTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/foraging/Leek.png")));
    }


    public Texture getSpaceImageTexture() {
        return spaceImageTexture;
    }

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null)
            gameAssetManager = new GameAssetManager();
        return gameAssetManager;
    }

    public Skin getSkin() {
        SeedRegistry seedRegistry = SeedRegistry.getInstance();
        foragingLoader = new ForagingLoader();
        return skin;
    }

    public Skin getStardewSkin() {
        return stardewSkin;
    }

    public TextureRegion getTexture(String path) {
        return new TextureRegion(new Texture(Gdx.files.internal(path)));
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
        TextureRegion fiber = new TextureRegion(new Texture("assets/resource/Fiber.png"));
        TextureRegion stone = new TextureRegion(new Texture("assets/resource/Stone.png"));
        TextureRegion wood = new TextureRegion(new Texture("assets/resource/Wood.png"));
        TextureRegion coal = new TextureRegion(new Texture("assets/resource/Coal.png"));
        TextureRegion clay = new TextureRegion(new Texture("assets/resource/Clay.png"));
        TextureRegion battery_pack = new TextureRegion(new Texture("assets/resource/Battery_Pack.png"));
        TextureRegion bone_fragment = new TextureRegion(new Texture("assets/resource/Bone_Fragment.png"));
        TextureRegion cinder_shard = new TextureRegion(new Texture("assets/resource/Cinder_Shard.png"));
        TextureRegion copper_bar = new TextureRegion(new Texture("assets/resource/Copper_Bar.png"));
        TextureRegion copper_ore = new TextureRegion(new Texture("assets/resource/Copper_Ore.png"));
        TextureRegion gold_bar = new TextureRegion(new Texture("assets/resource/Gold_Bar.png"));
        TextureRegion gold_ore = new TextureRegion(new Texture("assets/resource/Gold_Ore.png"));
        TextureRegion hard_wood = new TextureRegion(new Texture("assets/resource/Hardwood.png"));
        TextureRegion iridium_bar = new TextureRegion(new Texture("assets/resource/Iridium_Bar.png"));
        TextureRegion iridium_ore = new TextureRegion(new Texture("assets/resource/Iridium_Ore.png"));
        TextureRegion iron_bar = new TextureRegion(new Texture("assets/resource/Iron_Bar.png"));
        TextureRegion iron_ore = new TextureRegion(new Texture("assets/resource/Iron_Ore.png"));
        TextureRegion radioactive_bar = new TextureRegion(new Texture("assets/resource/Radioactive_Bar.png"));
        TextureRegion radioactive_ore = new TextureRegion(new Texture("assets/resource/Radioactive_Ore.png"));
        TextureRegion refined_quartz = new TextureRegion(new Texture("assets/resource/Refined_Quartz.png"));


        resources.put("fiber", new ResourceItem("naturalResource", "Fiber", fiber, 1, 7));
        resources.put("stone", new ResourceItem("naturalResource", "Stone", stone, 1, 7));
        resources.put("wood", new ResourceItem("naturalResource", "Wood", wood, 1, 7));
        resources.put("coal", new ResourceItem("naturalResource", "Coal", coal, 1, 7));
        resources.put("clay", new ResourceItem("naturalResource", "Clay", clay, 1, 7));
        resources.put("battery_pack", new ResourceItem("naturalResource", "Battery_Pack", battery_pack, 1, 7));
        resources.put("bone_fragment", new ResourceItem("naturalResource", "Bone_Fragment", bone_fragment, 1, 7));
        resources.put("cinder_shard", new ResourceItem("naturalResource", "Cinder_Shard", cinder_shard, 1, 7));
        resources.put("copper_bar", new ResourceItem("naturalResource", "Copper_Bar", copper_bar, 1, 7));
        resources.put("copper_ore", new ResourceItem("naturalResource", "Copper_Ore", copper_ore, 1, 7));
        resources.put("gold_bar", new ResourceItem("naturalResource", "Gold_Bar", gold_bar, 1, 7));
        resources.put("gold_ore", new ResourceItem("naturalResource", "Gold_Ore", gold_ore, 1, 7));
        resources.put("hard_wood", new ResourceItem("naturalResource", "Hardwood", hard_wood, 1, 7));
        resources.put("iridium_bar", new ResourceItem("naturalResource", "Iridium_Bar", iridium_bar, 1, 7));
        resources.put("iridium_ore", new ResourceItem("naturalResource", "Iridium_Ore", iridium_ore, 1, 7));
        resources.put("iron_bar", new ResourceItem("naturalResource", "Iron_Bar", iron_bar, 1, 7));
        resources.put("iron_ore", new ResourceItem("naturalResource", "Iron_Ore", iron_ore, 1, 7));
        resources.put("radioactive_bar", new ResourceItem("naturalResource", "Radioactive_Bar", radioactive_bar, 1, 7));
        resources.put("radioactive_ore", new ResourceItem("naturalResource", "Radioactive_Ore", radioactive_ore, 1, 7));
        resources.put("refined_quartz", new ResourceItem("naturalResource", "Refined_Quartz", refined_quartz, 1, 7));

    }

    public List<FarmInOutPoint> getExitPointList() {
        return exitPointList;
    }

    public Texture getForagingTexture(String forageName) {
        if (!foragingItem.containsKey(forageName)) {
            return null;
        }
        return foragingItem.get(forageName);
    }

    public HashMap<String, ResourceItem> getNaturalResourceList() {
        return resources;
    }

    public void dispose() {
        if (spaceImageTexture != null) {
            spaceImageTexture.dispose();
        }
        if (buffIconTexture != null && buffIconTexture.getTexture() != null) {
            buffIconTexture.getTexture().dispose();
        }

        if (eatingEffectTexture != null && eatingEffectTexture.getTexture() != null) {
            eatingEffectTexture.getTexture().dispose();
        }

        if (refrigeratorTexture != null && refrigeratorTexture.getTexture() != null) {
            refrigeratorTexture.getTexture().dispose();
        }

        if (defaultIngredientTexture != null && defaultIngredientTexture.getTexture() != null) {
            defaultIngredientTexture.getTexture().dispose();
        }

        for (TextureRegion texture : foodTextures.values()) {
            if (texture != null && texture.getTexture() != null) {
                texture.getTexture().dispose();
            }
        }
    }



    private void loadEffectTextures() {
        buffIconTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/food/Burnt.png")));
        eatingEffectTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/food/Oil_of_Garlic_Buff.png")));
    }

    private void loadFoodTextures() {
        foodTextures = new HashMap<>();
        foodTextures.put("FriedEgg", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Fried_Egg.png"))));
        foodTextures.put("BakedFish", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Baked_Fish.png"))));
        foodTextures.put("Salad", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Salad.png"))));
        foodTextures.put("Omelet", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Omelet.png"))));
        foodTextures.put("PumpkinPie", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Pumpkin_Pie.png"))));
        foodTextures.put("Spaghetti", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Spaghetti.png"))));
        foodTextures.put("Pizza", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Pizza.png"))));
        foodTextures.put("Tortilla", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Tortilla.png"))));
        foodTextures.put("MakiRoll", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Maki_Roll.png"))));
        foodTextures.put("TripleShotEspresso", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Triple_Shot_Espresso.png"))));
        foodTextures.put("Cookie", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Cookie.png"))));
        foodTextures.put("HashBrowns", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Hashbrowns.png"))));
        foodTextures.put("Pancakes", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Pancakes.png"))));
        foodTextures.put("FruitSalad", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Fruit_Salad.png"))));
        foodTextures.put("RedPlate", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Red_Plate.png"))));
        foodTextures.put("Bread", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Bread.png"))));
        foodTextures.put("SalmonDinner", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Salmon_Dinner.png"))));
        foodTextures.put("VegetableMedley", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Vegetable_Medley.png"))));
        foodTextures.put("FarmersLunch", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Farmer%27s_Lunch.png"))));
        foodTextures.put("SurvivalBurger", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Survival_Burger.png"))));
        foodTextures.put("DishOTheSea", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Dish_O%27_The_Sea.png"))));
        foodTextures.put("SeaformPudding", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Seafoam_Pudding.png"))));
        foodTextures.put("MinersTreat", new TextureRegion(new Texture(Gdx.files.internal("assets/food/Miner%27s_Treat.png"))));
    }


    private void loadObjectTextures() {
        refrigeratorTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/food/refrigerator.jpg")));
    }

    public TextureRegion getFoodTexture(String foodId) {
        return foodTextures.getOrDefault(foodId, foodTextures.get("FriedEgg"));
    }

    public TextureRegion getIngredientTexture(String ingredientId) {
        return defaultIngredientTexture;
    }

    public TextureRegion getRefrigeratorTexture() {
        return refrigeratorTexture;
    }

    public TextureRegion getBuffIconTexture() {
        return buffIconTexture;
    }

    public TextureRegion getEatingEffectTexture() {
        return eatingEffectTexture;
    }


}


