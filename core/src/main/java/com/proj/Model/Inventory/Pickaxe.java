package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pickaxe extends Tool {

    public Pickaxe(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.PICKAXE, level);
    }

    @Override
    protected float getBaseEnergyCost() {
        return 5.0f; // Base energy cost for Pickaxe
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Logic for using pickaxe on a tile
        // For example, break stones, clear hoed land
        return true;
    }

    @Override
    public void use() {
        super.use();
        // Additional pickaxe-specific use logic
    }
}
