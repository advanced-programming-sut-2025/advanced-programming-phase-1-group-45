package models.MapElements.Tile.TileFeatures;

import models.MapElements.Tile.Tile;
import models.MapElements.crops.ForagingCrop;

public class hasForagingCrop extends hasForaging implements TileFeature {
    private ForagingCrop foragingCrop;

    public hasForagingCrop(ForagingCrop foragingCrop, Tile tile) {
        super(tile);
        this.foragingCrop = foragingCrop;
    }

    public void collectForagingElement() {
        foragingCrop.saveInInventory(1);
        super.getTile().removeFeature(hasForaging.class);
        super.getTile().setSymbol('.');
    }
}
