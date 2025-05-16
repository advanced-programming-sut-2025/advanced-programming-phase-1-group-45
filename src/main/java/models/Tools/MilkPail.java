package models.Tools;

import models.MapElements.Tile.Tile;
import models.Player;

public class MilkPail extends Tool {

    public MilkPail(int energy) {
        super("MilkPail", energy);
    }

    @Override
    public void useTool(Tile targetTile, Player player) {

    }

    public void decreaseEnergy() {

    }
}
