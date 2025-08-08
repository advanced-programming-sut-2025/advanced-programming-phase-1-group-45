package com.proj.Model.inventoryItems.inventoryItems.crops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.proj.Model.inventoryItems.crops.CropData;
import com.proj.Map.Season;

public class CropRegistry {
    private static CropRegistry instance;
    private  Array<com.proj.Model.inventoryItems.crops.CropData> allData;

    private CropRegistry() {
        FileHandle file = Gdx.files.internal("assets/data/crop_data.json");
        Json json = new Json();
        try {
            json.setElementType(com.proj.Model.inventoryItems.crops.CropData.class, "season", Season.class);
            json.setElementType(com.proj.Model.inventoryItems.crops.CropData.class, "texturePaths", String.class);
            allData = file.exists()
                ? json.fromJson(Array.class, com.proj.Model.inventoryItems.crops.CropData.class, file)
                : new Array<>();

            if (allData.size == 0) System.exit(0);
            for (com.proj.Model.inventoryItems.crops.CropData data : allData) {
                data.loadTextures();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static synchronized CropRegistry getInstance() {
        if (instance == null) {
            instance = new CropRegistry();
        }
        return instance;
    }

    public com.proj.Model.inventoryItems.crops.CropData get(String cropId) {
        if (cropId == null || cropId.isEmpty()) return null;

        for (com.proj.Model.inventoryItems.crops.CropData td : allData) {
            if (cropId.replaceAll("_", " ").equalsIgnoreCase(td.getName())) return td;
        }
        return null;
    }

    public Array<CropData> getAllCropData() {
        return allData;
    }
}
