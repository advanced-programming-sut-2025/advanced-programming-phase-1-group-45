package models.Tools.Backpack;

import models.Player;

public abstract class BackPackItem {
    public abstract String getItemName();
    public abstract void saveInInventory(int amount, Player player);
}
