package models.crops;

import models.Enums.Season;
import models.crops.Crop.PlantInfo;

public class PlantSeed {
    private final String name;
    private final PlantInfo plant;

    public PlantSeed(PlantInfo plant) {
        this.plant = plant;
        this.name = plant.getSource();
    }
    public String getName() {
        return name;
    }
    public PlantInfo getPlant() {
        return plant;
    }
}
