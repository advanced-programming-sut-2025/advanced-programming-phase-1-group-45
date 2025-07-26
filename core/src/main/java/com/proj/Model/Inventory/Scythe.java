package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Scythe extends Tool {

    public Scythe(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.SCYTHE, level);
    }

    @Override
    protected float getBaseEnergyCost() {
        return 2.0f; // Base energy cost for Scythe
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using scythe on a tile
        // For example, cut grass or harvest crops
        return true;
    }

    @Override
    public void use() {
        super.use();
        // Additional scythe-specific use logic
    }
}
