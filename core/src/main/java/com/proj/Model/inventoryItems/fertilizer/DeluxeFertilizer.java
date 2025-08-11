package com.proj.Model.inventoryItems.fertilizer;

import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;

public class DeluxeFertilizer extends InventoryItem {
    public DeluxeFertilizer() {
        super("Deluxe_Fertilizer", "Deluxe Fertilizer",
            GameAssetManager.getGameAssetManager().getTexture("assets/fertilize2.png"),
            false,1);
    }

    @Override
    public void use() {

    }
    public void useOnTile(float x, float y) {

    }
}
