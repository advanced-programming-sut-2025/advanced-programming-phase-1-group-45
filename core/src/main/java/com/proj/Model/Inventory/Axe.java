package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Axe extends Tool {

    public Axe(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.AXE, level);
    }

    @Override
    protected float getBaseEnergyCost() {
        return 5.0f; // Base energy cost for Axe
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using axe on a tile - cutting trees, breaking branches
        // Return true if the action was successful
        return true;
    }

    @Override
    public void use() {
        super.use();
        // Additional axe-specific use logic if needed
    }
}
