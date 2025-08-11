package com.proj.Model.inventoryItems.fertilizer;

import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.InventoryItemFactory;

public class BasicFertilizer extends InventoryItem {
    public BasicFertilizer() {
        super("Basic_Fertilizer", "Basic Fertilizer",
            GameAssetManager.getGameAssetManager().getTexture("assets/fertilize1.png"),
            false,1);
    }

    @Override
    public void use() {

    }

    public void useOnTile(int x, int y) {

    }
}

