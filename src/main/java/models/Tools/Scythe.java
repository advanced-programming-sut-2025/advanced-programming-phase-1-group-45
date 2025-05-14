package models.Tools;

import models.Enums.Tile;
import models.User;

public class Scythe extends Tool {

    public Scythe(int energy) {
        super("Scythe", energy);
    }

    public void useTool() {
       // User.decreaseEnergy(2);
    }

    @Override
    public void useTool(Tile targetTile) {

    }

    @Override
    public void decreaseEnergy() {

    }
}
