package models.MapElements.crops;

import models.GameSession;
import models.Tools.Backpack.BackPackItem;

public class ForagingMineral implements BackPackItem {
    private final String name;
    private final String description;
    private final int sellPrice;

    public ForagingMineral(String name, String description, int sellPrice) {
        this.name = name;
        this.description = description;
        this.sellPrice = sellPrice;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSellPrice() {
        return sellPrice;
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
