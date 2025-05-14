package models.MapElement.crops.Plant;

import managers.TimeManager;

import java.util.Arrays;

public class PlantGrow {
    public void grow(PlantInMap crop) {
        if (Arrays.stream(crop.getSeason()).
                noneMatch(season -> season == TimeManager.getInstance().getSeason())) {
            return;
        }
        if (!crop.isHarvestAble()) {
            crop.advanceDayInStage();
        }
    }
}
