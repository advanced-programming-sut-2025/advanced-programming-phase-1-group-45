package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.crops.Crop.PlantGrow;
import models.crops.Crop.PlantInMap;


public class hasPlant implements TileFeature, UnWalkAble {
    private final Tile tile;
    private PlantInMap crop;
    private PlantGrow plantGrowStrategy;

    public PlantInMap getCrop() {
        return crop;
    }

    public PlantGrow getCropGrowStrategy() {
        return plantGrowStrategy;
    }

    public hasPlant(Tile tile, PlantInMap plantInMap) {
        this.tile = tile;
        this.crop = plantInMap;
        PlantGrow plantGrowStrategy = new PlantGrow();
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void removeCrop(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y()) {
            tile.removeFeature(hasPlant.class);
            //make sure tile display changed after this
        }
    }


}
