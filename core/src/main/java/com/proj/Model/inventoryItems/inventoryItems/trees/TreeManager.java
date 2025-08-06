package com.proj.Model.inventoryItems.inventoryItems.trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.trees.Tree;
import com.proj.Model.inventoryItems.trees.TreeData;
import com.proj.Model.inventoryItems.trees.TreeRegistry;
import com.proj.map.GameMap;
import com.proj.map.Season;

public class TreeManager {
    private final Array<com.proj.Model.inventoryItems.trees.Tree> trees = new Array<>();
    private GameMap map;

    public void setMap(GameMap map) {
        this.map = map;
    }

    public boolean plantFromSeed(String seedId, int tileX, int tileY) {
        TreeData data = TreeRegistry.getInstance().getBySeed(seedId);
        if (data == null) {
            System.out.println("Tree " + seedId + " not found");
            return false;}

        if (!map.canPlantInTile(tileX, tileY)) {
            return false;
        }

        trees.add(new com.proj.Model.inventoryItems.trees.Tree(data, tileX, tileY));
        map.getTile(tileX, tileY).setOccupied(true);
        map.getTile(tileX, tileY).setPassable(false);
        return true;
    }

    public void updateDaily(Season season) {
        for (com.proj.Model.inventoryItems.trees.Tree tree : trees) {
            if (tree.getTreeData().getSeasons().contains(season, false)) {
                tree.grow();
            }

            if (tree.isFullyGrown() &&
                tree.isFruitTree() &&
                tree.getTreeData().getSeasons().contains(season, false) &&
                !tree.hasProduct()) {
                tree.setHasProduct(true);
            }
        }
    }

    public InventoryItem harvestAt(int tileX, int tileY) {
        com.proj.Model.inventoryItems.trees.Tree tree = getTreeAt(tileX, tileY);
        if (tree == null || !tree.hasProduct()) return null;

        return tree.harvest();
    }

    public Array<InventoryItem> chopAt(int tileX, int tileY) {
        com.proj.Model.inventoryItems.trees.Tree tree = getTreeAt(tileX, tileY);
        if (tree == null) return null;

        Array<InventoryItem> items = tree.chop();
        map.getTile(tileX, tileY).setOccupied(false);
        map.getTile(tileX, tileY).setPassable(true);
        trees.removeValue(tree, true);
        return items;
    }

    public void renderAll(SpriteBatch batch, Season season) {
        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        for (com.proj.Model.inventoryItems.trees.Tree tree : trees) {
            TextureRegion tex = tree.getTexture(season);;
            if (tex == null) continue;

            float texW = tex.getRegionWidth() / 2f;
            float texH = tex.getRegionHeight() / 2f;

            float drawX = tree.getX() * tileW + (tileW - texW) / 2f;
            float drawY = tree.getY() * tileH;
            batch.draw(
                tex,
                drawX, drawY,
                texW, texH
            );

        }
    }


    public com.proj.Model.inventoryItems.trees.Tree getTreeAt(int x, int y) {
        for (com.proj.Model.inventoryItems.trees.Tree tree : trees) {
            if (tree.getX() == x && tree.getY() == y) {
                return tree;
            }
        }
        return null;
    }

    public Array<Tree> getTrees() {
        return trees;
    }
}
