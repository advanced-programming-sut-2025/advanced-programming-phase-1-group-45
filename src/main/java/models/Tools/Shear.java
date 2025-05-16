package models.Tools;

import models.MapElements.Tile.Tile;
import models.Player;

public class Shear extends Tool{
    public Shear(int energy) {
        super("Shear", 4);
    }


    @Override
    public void useTool(Tile targetTile, Player player) {
        player.getEnergy().consumeEnergy(4);
    }

    public void decreaseEnergy() {

    }
}
