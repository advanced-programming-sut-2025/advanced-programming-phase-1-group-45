package com.proj.Model.inventoryItems.inventoryItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;

public class FruitItem extends InventoryItem {
    private final int energy;
    private final int sellPrice;

    public FruitItem(String id, String name, TextureRegion texture, int quantity, int energy, int sellPrice) {
        super(id, name, texture, true, 99);
        this.energy = energy;
        this.sellPrice = sellPrice;
        setQuantity(quantity);
    }

    public int getEnergy() { return energy; }
    public int getSellPrice() { return sellPrice; }

    @Override
    public boolean isStackable() { return true; }
    @Override
    public int getMaxStackSize() { return 99; }

    @Override
    public void use() {

    }
}
