package managers.Progress.Farming;

import managers.TimeManager;
import models.MapElements.crops.Plant.PlantInMap;

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
