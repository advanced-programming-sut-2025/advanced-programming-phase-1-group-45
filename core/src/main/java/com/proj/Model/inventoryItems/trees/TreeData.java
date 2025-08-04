package com.proj.Model.inventoryItems.trees;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.proj.Model.GameAssetManager;
import com.proj.map.Season;

public class TreeData {
    private String id;
    private String name;
    private int growthStage;
    private int[] daysPerStage;
    private Array<Season> season;
    private Array<String> texturePaths;
    private String product;
    private int[] productCount = {1,2};
    private Array<String> chopProduct;
    private int[] chopProductCount = {1,2};
    private Season harvestSeason;
    private boolean isFruitTree;
    private String source;
    private int fruitHarvestCycle;
    private int fruitBaseSellPrice;
    private boolean isFruitEdible;
    private int fruitEnergy;

    private Array<TextureRegion> growthTextures;
    private TextureRegion fruitTexture;
    private ObjectMap<Season, TextureRegion> seasonalTextures;
    private TextureRegion lightningTexture;

    public void loadTextures() {
        growthTextures = new Array<>();
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();

        if (texturePaths == null || texturePaths.size == 0) {
            Gdx.app.error("TreeData", "No texture paths for tree: " + id);
            return;
        }

        for (int i = 0; i < growthStage - 1; i++) {
            if (i < texturePaths.size) {
                growthTextures.add(assetManager.getTexture(texturePaths.get(i)));
            }
        }

        seasonalTextures = new ObjectMap<>();
        int seasonalIndex = isFruitTree ? growthStage : growthStage - 1;

        if (texturePaths.size > seasonalIndex) {
            seasonalTextures.put(Season.SPRING, assetManager.getTexture(texturePaths.get(seasonalIndex++)));
        }
        if (texturePaths.size > seasonalIndex) {
            seasonalTextures.put(Season.SUMMER, assetManager.getTexture(texturePaths.get(seasonalIndex++)));
        }
        if (texturePaths.size > seasonalIndex) {
            seasonalTextures.put(Season.FALL, assetManager.getTexture(texturePaths.get(seasonalIndex++)));
        }
        if (texturePaths.size > seasonalIndex) {
            seasonalTextures.put(Season.WINTER, assetManager.getTexture(texturePaths.get(seasonalIndex++)));
        }

        if (isFruitTree && texturePaths.size > growthStage) {
            fruitTexture = assetManager.getTexture(texturePaths.get(growthStage - 1));
        }

        if (texturePaths.size > seasonalIndex) {
            lightningTexture = assetManager.getTexture(texturePaths.get(texturePaths.size - 1));
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getGrowthStage() { return growthStage; }
    public int[] getDaysPerStage() { return daysPerStage; }
    public Array<Season> getSeasons() { return season; }
    public String getProduct() { return product; }
    public int[] getProductCount() { return productCount; }
    public Array<String> getChopProduct() { return chopProduct; }
    public int[] getChopProductCount() { return chopProductCount; }
    public Season getHarvestSeason() { return harvestSeason; }
    public boolean isFruitTree() { return isFruitTree; }
    public String getSource() { return source; }
    public int getFruitHarvestCycle() { return fruitHarvestCycle; }
    public int getFruitBaseSellPrice() { return fruitBaseSellPrice; }
    public boolean isFruitEdible() { return isFruitEdible; }
    public int getFruitEnergy() { return fruitEnergy; }

    public TextureRegion getTextureForStage(int stage) {
        if (stage < 0 || stage >= growthTextures.size) {
            return growthTextures.size > 0 ? growthTextures.first() : null;
        }
        return growthTextures.get(stage);
    }

    public TextureRegion getSeasonalTexture(Season season) {
        TextureRegion texture = seasonalTextures.get(season);
        if (texture == null && seasonalTextures.size > 0) {
            texture = seasonalTextures.values().next();
        }
        return texture;
    }

    public TextureRegion getFruitTexture() {
        return fruitTexture;
    }

    public TextureRegion getLightningTexture() {
        return lightningTexture;
    }
}
