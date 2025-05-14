package models.crops.Crop;

import managers.TimeManager;

import java.util.Arrays;

public class CropGrow {
    public void grow(CropInMap crop) {
        if (Arrays.stream(crop.getSeason()).
                noneMatch(season -> season == TimeManager.getInstance().getSeason())) {
            return;
        }
        if (!crop.isHarvestAble()) {
            crop.advanceDayInStage();
        }
    }
}
