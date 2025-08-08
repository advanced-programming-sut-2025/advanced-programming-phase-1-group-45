package com.proj.Model.inventoryItems.trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Map.GameMap;
import com.proj.Map.Season;

public class TreeManager {
    private final Array<Tree> trees = new Array<>();
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

        trees.add(new Tree(data, tileX, tileY));
        map.getTile(tileX, tileY).setOccupied(true);
        map.getTile(tileX, tileY).setPassable(false);
        return true;
    }

    public void updateDaily(Season season) {
        for (Tree tree : trees) {
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
        Tree tree = getTreeAt(tileX, tileY);
        if (tree == null || !tree.hasProduct()) return null;

        return tree.harvest();
    }

    public Array<InventoryItem> chopAt(int tileX, int tileY) {
        Tree tree = getTreeAt(tileX, tileY);
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

        for (Tree tree : trees) {
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


    public Tree getTreeAt(int x, int y) {
        for (Tree tree : trees) {
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
