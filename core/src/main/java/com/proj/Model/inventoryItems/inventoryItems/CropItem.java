package com.proj.Model.inventoryItems.inventoryItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.proj.Model.Inventory.InventoryItem;

public class CropItem extends InventoryItem {
    private final int energy;
    private final int sellPrice;
    private boolean isEdible;
    private List<Integer> growthStages;
    private int numGrowthStages;
    private int totalHarvestTime = 0;
    private boolean isOneTime;
    private int regrowthTime ;

    public CropItem(String id, String name,
                    TextureRegion texture, int quantity,
                    Integer energy, int sellPrice,
                    boolean isEdible) {
        super(id, name, texture, true, 99);
        this.energy = energy == null ? 0 : energy;
        this.sellPrice = sellPrice;
        setQuantity(quantity);
        this.isEdible = isEdible;
    }


    public boolean isEdible() {
        return isEdible;
    }

    public int getEnergy() {
        return energy;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public boolean isStackable() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 99;
    }

    @Override
    public void use() {

    }
}

