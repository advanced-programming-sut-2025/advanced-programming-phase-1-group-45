package models.MapElements;

import models.Player;
import models.Tools.Backpack.BackPackItem;

public class Stone extends BackPackItem {
    @Override
    public String getItemName() {
        return "Stone";
    }

    @Override
    public void saveInInventory(int amount, Player player) {
        player.getBackpack().addItemAmount(this, 1);
    }
}
