package models.MapElements.Tile.TileFeatures;


import models.MapElements.Tile.Tile;

public abstract class hasForaging implements TileFeature, UnWalkAble {
    private final Tile tile;

    hasForaging(Tile tile) {
        this.tile = tile;
    }

    public void collectForagingElement(Class<? extends hasForaging> clazz) {
        tile.removeFeature(clazz);
        tile.removeFeature(hasForaging.class);
    }

    //TODO
    //add to inventory
    //increase foraging ability

}
