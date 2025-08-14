package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.proj.Model.inventoryItems.ForagingItem;
import com.proj.map.Season;

import java.util.Arrays;

public class ForagingLoader {

    private Array<ForagingItem> foragingCrops = new Array<>();
    private Array<ForagingItem> foragingMinerals = new Array<>();

    public ForagingLoader() {
        loadItemsFromJSON();
        loadMineralsFromJSON();
    }

    private void loadItemsFromJSON() {
        Json json = new Json();
        FileHandle file = Gdx.files.internal("assets/forageItem/ForagingItemData.json");

        if (file.exists()) {
            Array<ForagingItemData> itemsData = json.fromJson(Array.class, ForagingItemData.class, file);

            for (ForagingItemData data : itemsData) {
                String texKey = data.name
                    .toLowerCase()
                    .trim()
                    .replaceAll("\\s+", "");

                TextureRegion texture = new TextureRegion(
                    GameAssetManager
                        .getGameAssetManager()
                        .getForagingTexture(texKey)
                );
                Season[] seasons = Arrays.stream(data.season)
                    .map(Season::valueOf)
                    .toArray(Season[]::new);

                foragingCrops.add(new ForagingItem(
                    data.name, seasons, data.baseSellPrice, data.energy, texture
                ));
            }
        }
    }

    private void loadMineralsFromJSON() {
        Json json = new Json();
        FileHandle file = Gdx.files.internal("assets/forageItem/foragingMinerals.json");

        if (file.exists()) {
            Array<ForagingMineralData> itemsData = json.fromJson(Array.class, ForagingMineralData.class, file);

            for (ForagingMineralData data : itemsData) {
                String texKey = data.name
                    .replaceAll("\\s+", "_");

                TextureRegion texture = new TextureRegion(
                    new Texture("assets/Mineral/" + texKey +".png")
                );

                Season[] seasons = new Season[1];
                seasons[0] = Season.values()[0];
                foragingMinerals.add(new ForagingItem(
                    data.name, seasons, data.baseSellPrice, 0, texture
                ));
            }
        }
    }

    static class ForagingItemData {
        String name;
        String[] season;
        int baseSellPrice;
        int energy;
    }
    static class ForagingMineralData {
        String name;
        String description;
        int baseSellPrice;
    }

    public Array<ForagingItem> getForagingCrops() {
        return foragingCrops;
    }
    public Array<ForagingItem> getForagingMinerals() {
        return foragingMinerals;
    }
}
