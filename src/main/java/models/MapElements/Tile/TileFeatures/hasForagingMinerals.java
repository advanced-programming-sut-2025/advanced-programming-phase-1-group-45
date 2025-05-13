package models.MapElements.Tile.TileFeatures;

import models.MapElements.Tile.Tile;
import models.crops.ForagingMineral;

public class hasForagingMinerals implements TileFeature, hasForaging {
    private ForagingMineral foragingMinerals;
    private final Tile tile;

    hasForagingMinerals(ForagingMineral foragingMinerals, Tile tile) {
        this.foragingMinerals = foragingMinerals;
        this.tile = tile;
    }

    @Override
    public void collectForagingElement() {

    }
}
