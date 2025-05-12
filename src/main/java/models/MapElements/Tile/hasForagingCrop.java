package models.MapElements.Tile;

import models.crops.ForagingCrop;

public class hasForagingCrop implements TileFeature, hasForaging {
    private ForagingCrop foragingCrop;
    private final Tile tile;

    hasForagingCrop(ForagingCrop foragingCrop, Tile tile) {
        this.foragingCrop = foragingCrop;
        this.tile = tile;
    }

    @Override
    public void collectForagingElement() {

    }
}
