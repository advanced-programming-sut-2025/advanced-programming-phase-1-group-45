package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shears extends Tool {

    public Shears(String id, String name, TextureRegion texture) {
        super(id, name, texture, ToolType.SHEARS, 1);
    }

    @Override
    protected float getBaseEnergyCost() {
        return 4.0f;
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {

        boolean sheepFound = checkForShearableSheep(tileX, tileY);

        if (sheepFound) {
            return true;
        }
        return false;
    }

    private boolean checkForShearableSheep(int tileX, int tileY) {

        return true;
    }
}

