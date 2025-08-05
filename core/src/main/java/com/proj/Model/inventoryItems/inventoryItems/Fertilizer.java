package com.proj.Model.inventoryItems.inventoryItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;

public class Fertilizer extends InventoryItem {
    public Fertilizer(String id, String name,
                      TextureRegion textureRegion, String quantity) {
        super(id, name, textureRegion, false, 1);
    }
    @Override
    public void use() {

    }
}
