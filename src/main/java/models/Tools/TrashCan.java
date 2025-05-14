package models.Tools;

import models.Enums.Tile;

public class TrashCan extends Tool {
    public TrashCan(String itemName, int energy) {
        super(itemName, energy);
    }

    @Override
    public void useTool(Tile targetTile) {

    }

    @Override
    public void decreaseEnergy() {

    }
}
