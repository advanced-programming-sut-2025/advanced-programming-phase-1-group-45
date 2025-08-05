package com.proj.Model.inventoryItems.fertilizer;

import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.InventoryItemFactory;

public class BasicFertilizer extends InventoryItem {
    public BasicFertilizer() {
        super("Basic Fertilizer", "fertilizer_basic",
            GameAssetManager.getGameAssetManager().getTexture("assets/basic_fertilizer.png"),
            false,1);
    }

    @Override
    public void use() {

    }
}

