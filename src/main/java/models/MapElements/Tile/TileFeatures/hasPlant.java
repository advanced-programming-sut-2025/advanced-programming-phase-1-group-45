package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import managers.Progress.Farming.PlantGrow;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.Events.SeasonChangedEvent;
import models.MapElements.Tile.Tile;
import models.MapElements.crops.Plant.PlantInMap;

import java.util.Arrays;


public class hasPlant implements TileFeature {
    private final Tile tile;
    private PlantInMap plant;
    private PlantGrow plantGrowStrategy;

    public PlantInMap getCrop() {
        return plant;
    }

    public PlantGrow getCropGrowStrategy() {
        return plantGrowStrategy;
    }

    public hasPlant(Tile tile, PlantInMap plantInMap) {
        this.tile = tile;
        this.plant = plantInMap;
        tile.setSymbol('p');
        tile.addFeature(canWater.class, new canWater(tile));
        PlantGrow plantGrowStrategy = new PlantGrow();
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void removeSeasonalCrop(SeasonChangedEvent event) {
        if (Arrays.stream(plant.getSeason()).
                noneMatch(season -> season == event.newSeason()) &&
                !plant.isHarvestAble()) {
            removeThisFeature();
        }
    }

    @Subscribe
    public void removeCropForDrought(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y() &&
                !plant.isHarvestAble()) {
            removeThisFeature();
            //make sure tile display changed after this
        }
    }

    private void removeThisFeature() {
        tile.removeFeature(hasPlant.class);
        tile.addFeature(isEmpty.class, new isEmpty());
        tile.removeFeature(canWater.class);
        tile.setSymbol('.');
    }

    private void harvestCrop() {
        plant.harvest();
    }
}

