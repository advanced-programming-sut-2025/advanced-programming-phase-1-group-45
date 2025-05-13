package models.crops.Crop;

import models.Enums.Season;
import models.MapElements.Tile.TileFeatures.canGrow;

public class CropInMap extends canGrow {
    private CropInfo crop;
    private boolean harvestAble = false;
    private final int[] growStages;
    private final Season[] seasons;
    int currentStage = 0;
    int daysInStage = 0;

    CropInMap(CropInfo crop) {
        this.crop = crop;
        growStages = crop.getStages();
        seasons = crop.getSeason();
    }

    public CropInfo getCropInfo() {
        return crop;
    }

    public Season[] getSeason() {
        return seasons;
    }

    public boolean isHarvestAble() {
        return harvestAble;
    }

    @Override
    public void advanceDayInStage() {
        daysInStage++;
        if (daysInStage == growStages[currentStage]) {
            advanceStage();
            daysInStage = 0;
        }
    }

    @Override
    public void advanceStage() {
        if (currentStage < growStages.length) {
            currentStage++;
        }
        if (currentStage == growStages.length) {
            harvestAble = true;
        }
    }

    @Override
    public int getDaysInCurrentStage() {
        return growStages[currentStage];
    }
}
