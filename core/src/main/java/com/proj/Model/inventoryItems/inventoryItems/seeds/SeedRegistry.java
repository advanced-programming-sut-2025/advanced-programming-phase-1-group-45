package com.proj.Model.inventoryItems.inventoryItems.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.proj.Model.inventoryItems.seeds.SeedData;
import com.proj.map.Season;

public class SeedRegistry {
    private static SeedRegistry instance;
    private final ObjectMap<String, com.proj.Model.inventoryItems.seeds.SeedData> seeds = new ObjectMap<>();

    public static SeedRegistry getInstance() {
        if (instance == null) instance = new SeedRegistry();
        return instance;
    }

    private SeedRegistry() {
        loadSeeds("assets/data/seed_data.json");
    }

    private void loadSeeds(String path) {
        Json json = new Json();
        FileHandle file = Gdx.files.internal(path);
        json.setElementType(com.proj.Model.inventoryItems.seeds.SeedData.class, "season", Season.class);
        try {
            if (file.exists()) {
                Array<com.proj.Model.inventoryItems.seeds.SeedData> seedArray = json.fromJson(Array.class, com.proj.Model.inventoryItems.seeds.SeedData.class, file);
                System.err.println("sizzzzzzzz" + seedArray.size);
                for (com.proj.Model.inventoryItems.seeds.SeedData data : seedArray) {
                    if (data.getId().equals("mahogany_seed")) {System.err.println("ItemRegistry: mahogany_seed");}
                    seeds.put(data.getId(), data);
                }
            } else {
                System.err.println("SeedRegistry Seed file not found: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public com.proj.Model.inventoryItems.seeds.SeedData getSeed(String seedId) {
        return seeds.get(seedId);
    }

    public Array<SeedData> getAllSeeds() {
        return new Array<>(seeds.values().toArray());
    }
}
