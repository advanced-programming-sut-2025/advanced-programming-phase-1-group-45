package models.Tools;

import models.MapElements.Tile.Tile;
import models.Player;

public class TrashCan extends Tool {

    public TrashCan(String itemName, int energy) {
        super(itemName, energy);
    }

    @Override
    public void useTool(Tile targetTile, Player player) {

    }

    public void decreaseEnergy() {

    }
}
