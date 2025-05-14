package models.Tools;

import models.MapElements.Tile.Tile;

public class MilkPail extends Tool {

    public MilkPail(int energy) {
        super("MilkPail", energy);
    }

    @Override
    public void useTool(Tile targetTile) {

    }

    @Override
    public void decreaseEnergy() {

    }
}
