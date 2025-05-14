package models.MapElements.Tile.TileFeatures;

import models.MapElements.Tile.Tile;
import models.MapElements.crops.ForagingCrop;

public class hasForagingCrop extends hasForaging implements TileFeature {
    private ForagingCrop foragingCrop;

    hasForagingCrop(ForagingCrop foragingCrop, Tile tile) {
        super(tile);
        this.foragingCrop = foragingCrop;
    }

    public void collectForagingElement() {
        super.collectForagingElement(this.getClass());
    }
}
