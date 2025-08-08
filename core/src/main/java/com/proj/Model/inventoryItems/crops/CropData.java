package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.GameAssetManager;
import com.proj.Map.Season;

public class CropData {
    private String name;
    private int[] growthStages;
    private Integer totalGrowthTime;
    private boolean isOneTimeHarvest;
    private Integer regrowthTime;
    private Integer baseSellPrice;
    private boolean isEdible;
    private Integer energy;
    private Array<Season> season;
    private boolean canBecomeGiant;
    private Array<String> texturePaths;
    private TextureRegion giantTexture;
    private String productTexturePath;
    private Integer[] productCount = {1, 1};

    private Array<TextureRegion> growthTextures;
    private TextureRegion hasProductTexture;

    public Integer getTotalGrowthTime() {
        totalGrowthTime = 0;
        for (int i = 0; i < growthStages.length; i++) {
            totalGrowthTime += growthStages[i];
        }
        return totalGrowthTime;
    }

    public String getProductTexturePath() {
        String nameInPath = name.replaceAll("\\s+", "_");
        if (name.equalsIgnoreCase("CranBerry")) nameInPath = "CranBerries";
        return productTexturePath = "assets/crops/" + nameInPath + ".png";
    }

    public TextureRegion getGiantTexture() {
        return giantTexture;
    }

    public Array<String> getTexturePaths() {
        return texturePaths;
    }

    public boolean isCanBecomeGiant() {
        return canBecomeGiant;
    }

    public Array<Season> getSeason() {
        return season;
    }


    public Integer getEnergy() {
        return energy;
    }

    public Integer[] getProductCount() {
        return productCount;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public Integer getBaseSellPrice() {
        return baseSellPrice;
    }

    public int getRegrowthTime() {
        return regrowthTime == null ? 0 : regrowthTime;
    }

    public boolean isOneTimeHarvest() {
        return isOneTimeHarvest;
    }

    public int[] getGrowthStages() {
        return growthStages;
    }

    public String getName() {
        return name;
    }

    public boolean canBecomeGiant() {
        return canBecomeGiant;
    }

    public void loadTextures() {
        growthTextures = new Array<>();
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();

        if (texturePaths == null || texturePaths.size == 0) {
            Gdx.app.error("TreeData", "No texture paths for tree: " + name);
            return;
        }

        for (Integer i = 0; i < growthStages.length; i++) {
            if (i < texturePaths.size) {
                growthTextures.add(assetManager.getTexture(texturePaths.get(i) + ".png"));
            }
        }


        if (texturePaths.size >= growthStages.length) {
            hasProductTexture = assetManager.getTexture(texturePaths.get(growthStages.length) + ".png");
        }

        if (canBecomeGiant) {
            String nameInPath = name.replaceAll("\\s+", "_");
            this.giantTexture = assetManager.getTexture("assets/crops/Giant_" + nameInPath + ".png");
        }
    }

    public TextureRegion getTextureForStage(Integer stage) {
        if (stage < 0 || stage >= growthTextures.size) {
            return growthTextures.size > 0 ? growthTextures.get(growthTextures.size - 1) : null;
        }
        return growthTextures.get(stage);
    }

    public TextureRegion getHasProductTexture() {
        return hasProductTexture;
    }
}
