package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MilkPail extends Tool {

    public MilkPail(String id, String name, TextureRegion texture) {
        super(id, name, texture, ToolType.MILK_PAIL, 1); // Milk pail doesn't have levels
    }

    @Override
    protected float getBaseEnergyCost() {
        return 4.0f; // Energy cost for Milk Pail
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using milk pail on an animal
        // Check if there is a milkable animal at the tile
        boolean animalFound = checkForMilkableAnimal(tileX, tileY);

        if (animalFound) {
            // Milking logic would go here
            return true;
        }
        return false;
    }

    private boolean checkForMilkableAnimal(int tileX, int tileY) {
        // Logic to check if there's a milkable animal at the tile
        // This would interact with the game's animal system
        return true; // Placeholder
    }
}
