package models.MapElement.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.MapElements.crops.Plant.PlantGrow;
import models.MapElements.crops.Plant.PlantInMap;


public class hasPlant implements TileFeature {
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
        tile.setSymbol('P');
        PlantGrow plantGrowStrategy = new PlantGrow();
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void removeCrop(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y() &&
                !crop.isHarvestAble()) {
            tile.removeFeature(hasPlant.class);
            tile.setSymbol('.');
            //make sure tile display changed after this
        }
    }
}
