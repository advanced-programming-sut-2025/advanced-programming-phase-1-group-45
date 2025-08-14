package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Control.WorldController;
import com.proj.map.GameMap;

public class WateringCan extends Tool {
    private int waterAmount;
    private int capacity;

    public WateringCan(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.WATERING_CAN, level);
        setCapacityByLevel(level);
        this.waterAmount = capacity;
    }

    private void setCapacityByLevel(int level) {
        switch (level) {
            case 1: this.capacity = 40; break;
            case 2: this.capacity = 55; break;
            case 3: this.capacity = 70; break;
            case 4: this.capacity = 85; break;
            case 5: this.capacity = 100; break;
            default: this.capacity = 40;
        }
    }

    @Override
    protected float getBaseEnergyCost() {
        return 5.0f;
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        try {
            GameMap map = WorldController.getInstance().getGameMap();
            map.getFarmingController().getCropManager().getCropAt(tileX, tileY).water();
        } catch (Exception e) {
            System.err.println("Error in waterAt: " + e.getMessage());
        }
        if (waterAmount > 0) {
            waterAmount--;
            return true;
        }
        return false;
    }



    @Override
    public void upgrade() {
        super.upgrade();
        setCapacityByLevel(getLevel());
        this.waterAmount = capacity;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getCapacity() {
        return capacity;
    }
}
