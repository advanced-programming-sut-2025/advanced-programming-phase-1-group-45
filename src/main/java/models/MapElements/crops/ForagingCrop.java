package models.MapElements.crops;

import models.Enums.Season;
import models.GameSession;
import models.MapElements.crops.Plant.PlantInfo;
import models.Tools.Backpack.BackPackItem;
import models.Tools.Backpack.Backpack;

public class ForagingCrop implements BackPackItem {
    private String name;
    private Season[] seasons;
    private int sellPrice;
    private int energy;
    public ForagingCrop(String name, String[] seasons, int sellPrice, int energy) {
        this.name = name + "foraging crop";
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

    @Override
    public String getItemName() {
        return name;
    }

    @Override
    public void saveInInventory(int amount) {
        GameSession.getCurrentPlayer().getInventory().addItem(this, amount);
    }
}
