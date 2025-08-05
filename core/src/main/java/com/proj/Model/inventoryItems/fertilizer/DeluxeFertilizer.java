package com.proj.Model.inventoryItems.fertilizer;

import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;

public class DeluxeFertilizer extends InventoryItem {
    public DeluxeFertilizer() {
        super("Deluxe Fertilizer", "fertilizer_deluxe",
            GameAssetManager.getGameAssetManager().getTexture("assets/deluxe_fertilizer.png"),
            false,1);
    }

    @Override
    public void use() {

    }
}
