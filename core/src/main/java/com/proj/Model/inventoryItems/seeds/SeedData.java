package com.proj.Model.inventoryItems.seeds;

import com.badlogic.gdx.utils.Array;
import com.proj.Model.inventoryItems.SeedItem;
import com.proj.Map.Season;

public class SeedData {
    private String id;
    private String name;
    private String texturePath;
    private int maxStackSize = 99;
    private String plantId;
    private Array<String> season;
    private String crop_id;
    private String tree_id;
    private SeedItem.SeedType seedType;

    public Array<Season> getSeasonsAsEnum() {
        if (season == null) return new Array<>();
        Array<Season> result = new Array<>();
        for (int i = 0; i < season.size; i++) {
            result.add(Season.valueOf(String.valueOf(season.get(i))));
        }
        return result;
    }

    public boolean canPlantInSeason(Season currentSeason) {
        if (currentSeason == null) return false;
        for (String s : season) {
            if (Season.valueOf(s.toUpperCase()) == currentSeason) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public Array<String> getSeason() {
        return season;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getName() {
        return name;
    }

    public String getPlantId() {
        if (tree_id != null ){
            return tree_id;
        }
        return crop_id;
    }

    public SeedItem.SeedType getSeedType() {
        if (crop_id != null) return SeedItem.SeedType.CROP;
        return SeedItem.SeedType.TREE;
    }
}
