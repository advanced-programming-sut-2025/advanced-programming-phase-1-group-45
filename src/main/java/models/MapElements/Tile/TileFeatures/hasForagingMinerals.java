package models.MapElement.Tile.TileFeatures;

import models.MapElements.Tile.Tile;
import models.MapElements.crops.ForagingMineral;

public class hasForagingMinerals extends hasForaging implements TileFeature {
    private ForagingMineral foragingMinerals;
    private final Tile tile;

    hasForagingMinerals(ForagingMineral foragingMinerals, Tile tile) {
        super(tile);
        this.foragingMinerals = foragingMinerals;
        this.tile = tile;
    }

    public void collectForagingElement() {

    }
}
