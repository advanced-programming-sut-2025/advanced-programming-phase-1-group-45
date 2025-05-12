package models.MapElements.Tile;

import models.crops.Tree;

public class hasTree extends canGrow implements TileFeature, UnWalkAble {
    private Tree tree;
    private Tile tile;
    private boolean harvestAble;

    hasTree(Tile tile, Tree tree) {
        this.tree = tree;
        this.tile = tile;
        this.harvestAble = false;
    }

    @Override
    public void grow() {

    }
}
