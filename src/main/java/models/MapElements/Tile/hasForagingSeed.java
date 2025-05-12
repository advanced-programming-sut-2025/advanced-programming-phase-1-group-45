package models.MapElements.Tile;

import models.crops.ForagingCrop;
import models.crops.ForagingSeed;

public class hasForagingSeed implements TileFeature, hasForaging {
    private ForagingSeed foragingSeed;
    private final Tile tile;

    hasForagingSeed(ForagingSeed foragingSeed, Tile tile) {
        this.foragingSeed = foragingSeed;
        this.tile = tile;
    }

    @Override
    public void collectForagingElement() {

    }
}
