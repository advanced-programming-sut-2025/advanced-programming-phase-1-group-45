package models.MapElements.crops.Plant;

import models.Enums.Season;
import models.MapElements.Tile.TileFeatures.canGrow;

public class PlantInMap extends canGrow {
    private final PlantInfo crop;
    private boolean harvestAble = false;
    private boolean completeGrow = false;
    int daysInCycle = 0;
    private final int[] growStages;
    private final Season[] seasons;
    private boolean isFertilized = false;
    int currentStage = 0;
    int daysInStage = 0;

    public PlantInMap(PlantInfo crop) {
        this.crop = crop;
        growStages = crop.getStages();
        seasons = crop.getSeason();
    }

    public PlantInfo getCropInfo() {
        return crop;
    }

    public Season[] getSeason() {
        return seasons;
    }

    public boolean isFertilized() {
        return isFertilized;
    }

    public void fertilize() {
        isFertilized = true;
    }

    public boolean isHarvestAble() {
        return harvestAble;
    }

    @Override
    public void advanceDayInStage() {
        if (!completeGrow || crop.isOneTime()) {
            daysInStage++;
            if (daysInStage == growStages[currentStage]) {
                advanceStage();
                daysInStage = 0;
            }
        } else {
            daysInCycle++;
            if (daysInCycle == crop.getRegrowthTime()) {
                harvestAble = true;
                daysInCycle = 0;
            }
        }
    }

    @Override
    public void advanceStage() {
        if (currentStage < growStages.length) {
            currentStage++;
        }
        if (currentStage == growStages.length) {
            harvestAble = true;
            completeGrow = true;
        }
    }

    @Override
    public int getDaysInCurrentStage() {
        return growStages[currentStage];
    }
}
