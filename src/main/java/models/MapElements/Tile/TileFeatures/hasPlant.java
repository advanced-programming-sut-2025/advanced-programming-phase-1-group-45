package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.crops.Crop.CropGrow;
import models.crops.Crop.CropInMap;
import models.crops.Crop.CropInfo;


public class hasCrop implements TileFeature, UnWalkAble {
    private final Tile tile;
    private CropInMap crop;
    private CropGrow cropGrowStrategy;

    public CropInMap getCrop() {
        return crop;
    }

    public CropGrow getCropGrowStrategy() {
        return cropGrowStrategy;
    }

    hasCrop(Tile tile, CropInMap cropInMap) {
        this.tile = tile;
        this.crop = cropInMap;
        CropGrow cropGrowStrategy = new CropGrow();
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void removeCrop(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y()) {
            tile.removeFeature(hasCrop.class);
            //make sure tile display changed after this
        }
    }


}
