package models.MapElements.crops;

import models.Enums.Season;
import models.GameSession;
import models.MapElements.crops.Plant.PlantInfo;
import models.Player;
import models.Tools.Backpack.BackPackItem;

public class ForagingSeed extends BackPackItem {
    private final String name;
    private final Season[] seasons;

    public ForagingSeed(String name, String[] seasons) {
        this.name = name + "foraging seed";
        this.seasons = PlantInfo.extractSeasonsFromString(seasons);
    }

    public String getName() {
        return name;
    }

    public Season[] getSeasons() {
        return seasons;
    }

    @Override
    public String getItemName() {
        return name;
    }

    @Override
    public void saveInInventory(int amount, Player player) {
        player.getBackpack().addItemAmount(this, amount);
    }
}