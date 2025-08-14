package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SimpleItem extends InventoryItem {
    public SimpleItem(String id, String name, TextureRegion texture, boolean stackable, int maxStackSize) {
        super(id, name, texture, stackable, maxStackSize);
    }

    @Override
    public void use() {
    }
}
