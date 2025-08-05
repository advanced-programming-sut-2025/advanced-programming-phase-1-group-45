package com.proj.Model.inventoryItems.inventoryItems.seeds;

import com.badlogic.gdx.utils.ObjectMap;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.CropItem;
import com.proj.Model.inventoryItems.SeedItem;
import com.proj.Model.inventoryItems.crops.CropData;
import com.proj.Model.inventoryItems.crops.CropRegistry;
import com.proj.Model.inventoryItems.seeds.SeedData;
import com.proj.Model.inventoryItems.seeds.SeedRegistry;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    private static ItemRegistry instance;
    private final ObjectMap<String, InventoryItem> items = new ObjectMap<>();
    private List<SeedItem> seeds = new ArrayList<>();

    private ItemRegistry() {
        for (SeedData sd : SeedRegistry.getInstance().getAllSeeds()) {
            SeedItem item = new SeedItem(sd.getId(), sd.getName(),
                GameAssetManager.getGameAssetManager().getTexture(sd.getTexturePath()), 1,
                sd.getMaxStackSize(), sd.getSeedType(),
                sd.getPlantId(), sd.getSeasonsAsEnum());
            items.put(item.getId(), item);
            seeds.add(item);
        }

        try {
            for (CropData cd : CropRegistry.getInstance().getAllCropData()) {
                CropItem item = new CropItem(cd.getName().toLowerCase().replaceAll(" ", "_"),
                    cd.getName(),
                    GameAssetManager.getGameAssetManager().getTexture(cd.getProductTexturePath()),
                    1, cd.getEnergy(),
                    cd.getBaseSellPrice(), cd.isEdible());
                items.put(item.getId(), item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized ItemRegistry getInstance() {
        if (instance == null) {
            instance = new ItemRegistry();
        }
        return instance;
    }

    public InventoryItem get(String itemId) {
        return items.get(itemId);
    }

    public List<SeedItem> getSeeds() {
        return seeds;
    }
}
