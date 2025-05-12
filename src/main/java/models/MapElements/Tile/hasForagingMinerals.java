package models.MapElements.Tile;

import models.crops.ForagingCrop;
import models.crops.ForagingMinerals;

public class hasForagingMinerals implements TileFeature, hasForaging {
    private ForagingMinerals foragingMinerals;
    private final Tile tile;

    hasForagingMinerals(ForagingMinerals foragingMinerals, Tile tile) {
        this.foragingMinerals = foragingMinerals;
        this.tile = tile;
    }

    @Override
    public void collectForagingElement() {

    }
}
