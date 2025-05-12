package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.crops.CropGrowStrategy;
import models.crops.CropInfo;

public class hasCrop extends canGrow implements TileFeature, UnWalkAble {
    private Tile tile;
    private CropInfo cropInfo;
    private boolean harvestAble;
    private CropGrowStrategy cropGrowStrategy;

    hasCrop(Tile tile, CropInfo cropInfo) {
        this.tile = tile;
        this.cropInfo = cropInfo;
        CropGrowStrategy cropGrowStrategy = new CropGrowStrategy();
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void removeCrop(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y()) {
            tile.removeFeature(hasCrop.class);
            //make sure tile display changed after this
        }
    }

    @Override
    public void grow() {

    }
}
