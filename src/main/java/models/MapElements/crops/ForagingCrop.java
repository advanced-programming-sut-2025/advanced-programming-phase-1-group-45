package models.MapElements.crops;

import models.Enums.Season;
import models.MapElements.crops.Plant.PlantInfo;

public class ForagingCrop {
    private String name;
    private Season[] seasons;
    private int sellPrice;
    private int energy;
    public ForagingCrop(String name, String[] seasons, int sellPrice, int energy) {
        this.name = name;
        this.sellPrice = sellPrice;
        this.energy = energy;
        this.seasons = PlantInfo.extractSeasonsFromString(seasons);
    }
    public String getName() {
        return name;
    }
    public int getSellPrice() {
        return sellPrice;
    }
    public int getEnergy() {
        return energy;
    }
    public Season[] getSeasons() {
        return seasons;
    }
}
