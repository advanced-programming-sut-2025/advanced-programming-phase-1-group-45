package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MilkPail extends Tool {

    public MilkPail(String id, String name, TextureRegion texture) {
        super(id, name, texture, ToolType.MILK_PAIL, 1); // Milk pail doesn't have levels
    }

    @Override
    protected float getBaseEnergyCost() {
        return 4.0f;
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {

        boolean animalFound = checkForMilkableAnimal(tileX, tileY);

        if (animalFound) {
            return true;
        }
        return false;
    }

    private boolean checkForMilkableAnimal(int tileX, int tileY) {

        return true;
    }
}
