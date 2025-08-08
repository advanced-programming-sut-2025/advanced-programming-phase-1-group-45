package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.proj.Model.Inventory.*;
import com.proj.Model.inventoryItems.FruitItem;
import com.proj.Model.inventoryItems.ResourceItem;
import com.proj.Map.Season;

import java.util.HashMap;
import java.util.Map;

public class InventoryItemFactory {
    private static InventoryItemFactory instance;
    private final Map<String, ItemCreator> itemCreators = new HashMap<>();
    private final ObjectMap<String, ItemData> itemDataMap = new ObjectMap<>();

    // رابط کاربری برای ایجاد آیتم‌ها
    private interface ItemCreator {
        InventoryItem create(String id, int quantity);
    }

    // ساختار داده‌های آیتم‌ها
    private static class ItemData {
        String id;
        String name;
        String type;
        Season[] seasons;
        String texturePath;
        boolean stackable;
        int maxStackSize;
        int energy;
        int sellPrice;
        String toolType;
        int toolLevel;
        String treeId; // برای بذر درختان
    }

    private InventoryItemFactory() {
        loadItemData();
        registerCreators();
    }

    public static InventoryItemFactory getInstance() {
        if (instance == null) {
            instance = new InventoryItemFactory();
        }
        return instance;
    }

    private void loadItemData() {
        Json json = new Json();
        FileHandle file = Gdx.files.internal("assets/data/items.json");

        if (file.exists()) {
            Array<ItemData> items = json.fromJson(Array.class, ItemData.class, file);
            for (ItemData data : items) {
                itemDataMap.put(data.id, data);
            }
        }
    }

    private void registerCreators() {
        // ثبت سازنده‌ها برای انواع مختلف آیتم‌ها
        itemCreators.put("TOOL", this::createTool);
        itemCreators.put("SEED", this::createSeed);
        itemCreators.put("FRUIT", this::createFruit);
        itemCreators.put("RESOURCE", this::createResource);
//        itemCreators.put("TREE_SEED", this::createTreeSeed);
    }

    // متد اصلی برای ایجاد آیتم
    public static InventoryItem createItem(String itemId, int quantity) {
        return getInstance().createItemInternal(itemId, quantity);
    }

    private InventoryItem createItemInternal(String itemId, int quantity) {
        ItemData data = itemDataMap.get(itemId);
        if (data == null) {
            Gdx.app.error("ItemFactory", "Unknown item ID: " + itemId);
            return null;
        }

        ItemCreator creator = itemCreators.get(data.type);
        if (creator != null) {
            InventoryItem item = creator.create(itemId, quantity);
            if (item != null) {
                return item;
            }
        }

        Gdx.app.error("ItemFactory", "No creator for item type: " + data.type);
        return null;
    }

    // --- روش‌های ایجاد انواع خاص آیتم‌ها ---

    private InventoryItem createTool(String id, int quantity) {
        ItemData data = itemDataMap.get(id);
        TextureRegion texture = GameAssetManager.getGameAssetManager().getTexture(data.texturePath);
        InventoryItem inventoryItem = null;
        switch (data.toolType) {
            case "AXE":
                inventoryItem = new Axe(id, data.name, texture, data.toolLevel);
                break;
            case "HOE":
                inventoryItem = new Hoe(id, data.name, texture, data.toolLevel);
                break;
            case "WATERING_CAN":
                inventoryItem = new WateringCan(id, data.name, texture, data.toolLevel);
                break;
            case "SCYTHE":
                inventoryItem = new Scythe(id, data.name, texture, data.toolLevel);
                break;
            case "FISHING_ROD":
                inventoryItem = new FishingRod(id, data.name, texture, data.toolLevel);
                break;
        }

        return inventoryItem;
    }

    private InventoryItem createSeed(String id, int quantity) {
//        ItemData data = itemDataMap.get(id);
//        TextureRegion texture = GameAssetManager.getGameAssetManager().getTexture(data.texturePath);
//        return new SeedItem(id, data.name, texture, quantity, data.maxStackSize);
        return null;
    }

//    private InventoryItem createCrop(String id, int quantity) {
//        ItemData data = itemDataMap.get(id);
//        TextureRegion texture = GameAssetManager.getGameAssetManager().getTexture(data.texturePath);
//        return new CropItem(id, data.name, texture, quantity, data.energy, data.sellPrice,);
//    }

    private InventoryItem createFruit(String id, int quantity) {
        ItemData data = itemDataMap.get(id);
        TextureRegion texture = GameAssetManager.getGameAssetManager().getTexture(data.texturePath);
        return new FruitItem(id, data.name, texture, quantity, data.energy, data.sellPrice);
    }

    private InventoryItem createResource(String id, int quantity) {
        ItemData data = itemDataMap.get(id);
        TextureRegion texture = GameAssetManager.getGameAssetManager().getTexture(data.texturePath);
        return new ResourceItem(id, data.name, texture, quantity, data.sellPrice);
    }

//    private InventoryItem createTreeSeed(String id, int quantity) {
//        ItemData data = itemDataMap.get(id);
//        TextureRegion texture = GameAssetManager.getGameAssetManager().getTexture(data.texturePath);
//        return new TreeSeedItem(id, data.name, texture, quantity, data.treeId);
//    }
}
