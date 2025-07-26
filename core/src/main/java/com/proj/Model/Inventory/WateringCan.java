package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WateringCan extends Tool {
    private int waterAmount;
    private int capacity;

    public WateringCan(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.WATERING_CAN, level);
        setCapacityByLevel(level);
        this.waterAmount = capacity; // Start with full water
    }

    private void setCapacityByLevel(int level) {
        switch (level) {
            case 1: this.capacity = 40; break;  // Basic
            case 2: this.capacity = 55; break;  // Copper
            case 3: this.capacity = 70; break;  // Steel
            case 4: this.capacity = 85; break;  // Gold
            case 5: this.capacity = 100; break; // Iridium
            default: this.capacity = 40;
        }
    }

    @Override
    protected float getBaseEnergyCost() {
        return 5.0f; // Base energy cost for Watering Can
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using watering can on a tile - watering crops
        if (waterAmount > 0) {
            waterAmount--;
            return true;
        }
        return false; // Not enough water
    }

    public boolean fillWater() {
        // Logic to fill the watering can when used near water source
        waterAmount = capacity;
        return true;
    }

    @Override
    public void upgrade() {
        super.upgrade();
        setCapacityByLevel(getLevel());
        this.waterAmount = capacity; // Refill after upgrade
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getCapacity() {
        return capacity;
    }
}
