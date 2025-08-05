package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Control.WorldController;
import com.proj.Model.inventoryItems.ForagingItem;
import com.proj.Model.inventoryItems.ResourceItem;
import com.proj.Model.inventoryItems.crops.Crop;
import com.proj.Model.inventoryItems.crops.GiantCrop;
import com.proj.Model.inventoryItems.trees.Tree;
import com.proj.Player;
import com.proj.map.GameMap;
import com.proj.map.Tile;
import com.proj.map.TileType;

import java.awt.*;

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
        Tile tile = WorldController.getInstance().getGameMap().getLandLoader().getTiles()[tileX][tileY];
        if (tile.getType() == TileType.FIBER) {
            ResourceItem nr = WorldController.getInstance().getGameMap().pickNaturalResource(new Point(tileX, tileY));
            if (nr != null) {
                InventoryManager.getInstance().getPlayerInventory().addItem(nr);
                return true;
            }
        }
        if (tile.getType() == TileType.FORAGING) {
            ForagingItem collectedItem = WorldController
                .getInstance()
                .getForagingManager()
                .tryCollectItem(new Point(tileX, tileY), "Scythe");
            if (collectedItem != null) {
                InventoryManager.getInstance()
                    .getPlayerInventory()
                    .addItem(collectedItem);
                return true;
            }
        }

        System.err.println("use scyth on tile " + tileX + ", " + tileY);
        GameMap currentMap = WorldController.getInstance().getGameMap();
        if (currentMap.getCropManager() != null) {
            Crop crop = currentMap.getCropManager().getCropAt(tileX, tileY);
            GiantCrop giantCrop = currentMap.getCropManager().getGiantCropAt(tileX, tileY);
            if (crop != null || giantCrop != null) {
                InventoryItem item = currentMap.getCropManager().harvestAt(tileX, tileY);
                if (item != null) {
                    InventoryManager.getInstance().getPlayerInventory().addItem(item);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void use() {
        super.use();
        // Additional scythe-specific use logic
    }
}
