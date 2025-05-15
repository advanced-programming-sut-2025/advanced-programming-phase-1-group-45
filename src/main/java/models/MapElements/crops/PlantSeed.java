package models.MapElements.crops;

import models.MapElements.crops.Plant.PlantInfo;
import models.Tools.Backpack.BackPackItem;

public class PlantSeed implements BackPackItem {
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

    @Override
    public String getItemName() {
        return this.name;
    }

    @Override
    public void saveInInventory() {
        Player.getInventory().addItem(this);
    }
}
