package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.proj.Model.mapObjects.ForagingItem;
import com.proj.map.Season;

import java.util.Arrays;

public class ForagingLoader {

    private Array<ForagingItem> foragingCrops = new Array<>();

    public ForagingLoader() {
        loadItemsFromJSON();
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

    static class ForagingItemData {
        String name;
        String[] season;
        int baseSellPrice;
        int energy;
    }

    public Array<ForagingItem> getForagingCrops() {
        return foragingCrops;
    }
}
