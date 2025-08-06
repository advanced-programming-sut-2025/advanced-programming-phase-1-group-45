package com.proj.Model.inventoryItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryItemType;
import com.proj.Map.LandObject;

public class ResourceItem extends InventoryItem implements LandObject {
    private int sellPrice;

    public ResourceItem(String id, String name, TextureRegion texture, int quantity, int sellPrice) {
        super(id, name, texture, true, 99);
        this.sellPrice = sellPrice;
        setInventoryItemType(InventoryItemType.RESOURCE);
        setQuantity(quantity);
    }

    @Override
    public void use() {

    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    @Override
    public boolean canStack(InventoryItem other) {
        return other instanceof ResourceItem &&
            this.getId().equals(other.getId());
    }
}
