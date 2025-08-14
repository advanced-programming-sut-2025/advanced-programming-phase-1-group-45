package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Control.WorldController;
import com.proj.Model.inventoryItems.trees.Tree;
import com.proj.map.GameMap;

public class Axe extends Tool {

    public Axe(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.AXE, level);
    }

    @Override
    protected float getBaseEnergyCost() {
        return 5.0f;
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        GameMap currentMap = WorldController.getInstance().getGameMap();
        if (currentMap.getTreeManager() != null) {
            Tree tree = currentMap.getTreeManager().getTreeAt(tileX, tileY);
            if (tree != null) {
                Array<InventoryItem> item = currentMap.getTreeManager().chopAt(tileX, tileY);
                if (item != null) {
                    for (InventoryItem itemm : item) {
                    InventoryManager.getInstance().getPlayerInventory().addItem(itemm);}
                    return true;
                }
            }
        }        return true;
    }

    @Override
    public void use() {
        super.use();
    }
}
