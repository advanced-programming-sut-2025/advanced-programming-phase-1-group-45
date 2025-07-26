package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hoe extends Tool {

    public Hoe(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.HOE, level);
    }

    @Override
    protected float getBaseEnergyCost() {
        return 5.0f; // Base energy cost for Hoe
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using hoe on a tile
        // For example, convert GRASS to DIRT
        return true;
    }

    @Override
    public void use() {
        super.use();
        // Additional hoe-specific use logic
    }
}

