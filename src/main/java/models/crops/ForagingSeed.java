package models.crops;

import models.Enums.Season;
import models.crops.Crop.PlantInfo;

public class ForagingSeed {
    private final String name;
    private final Season[] seasons;

    public ForagingSeed(String name, String[] seasons) {
        this.name = name;
        this.seasons = PlantInfo.extractSeasonsFromString(seasons);
    }

    public String getName() {
        return name;
    }

    public Season[] getSeasons() {
        return seasons;
    }

}
