package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shears extends Tool {

    public Shears(String id, String name, TextureRegion texture) {
        super(id, name, texture, ToolType.SHEARS, 1); // Shears doesn't have levels
    }

    @Override
    protected float getBaseEnergyCost() {
        return 4.0f; // Energy cost for Shears
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using shears on an animal
        // Check if there is a sheep at the tile
        boolean sheepFound = checkForShearableSheep(tileX, tileY);

        if (sheepFound) {
            // Shearing logic would go here
            return true;
        }
        return false;
    }

    private boolean checkForShearableSheep(int tileX, int tileY) {
        // Logic to check if there's a shearable sheep at the tile
        // This would interact with the game's animal system
        return true; // Placeholder
    }
}

