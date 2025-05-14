package models.MapElements.Tile.TileFeatures;

import models.MapElements.Tile.Tile;
import models.crops.ForagingCrop;
import models.crops.ForagingSeed;

public class hasForagingSeed extends hasForaging implements TileFeature {
    private ForagingSeed foragingSeed;
    private final Tile tile;

    hasForagingSeed(ForagingSeed foragingSeed, Tile tile) {
        super(tile);
        this.foragingSeed = foragingSeed;
        this.tile = tile;
    }

    public void collectForagingElement() {

    }
}
