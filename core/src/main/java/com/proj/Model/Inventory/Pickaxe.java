package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Control.WorldController;
import com.proj.Model.mapObjects.NaturalResource;
import com.proj.map.Tile;
import com.proj.map.TileType;

import java.awt.*;

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
        Tile tile = WorldController.getInstance().getGameMap().getLandLoader().getTiles()[tileX][tileY];
        if (tile.getType() == TileType.STONE || tile.getType() == TileType.WOOD) {
            NaturalResource nr = WorldController.getInstance().getGameMap().pickNaturalResource(new Point(tileX, tileY));
            if (nr != null) {
                InventoryManager.getInstance().getPlayerInventory().addItem(nr);
                return true;
            }
        }
        return true;
    }

    @Override
    public void use() {
        super.use();
        // Additional pickaxe-specific use logic
    }
}
