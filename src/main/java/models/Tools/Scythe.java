package models.Tools;

import models.GameSession;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasPlant;

public class Scythe extends Tool {

    public Scythe(int energy) {
        super("Scythe", energy);
    }


    @Override
    public void useTool(Tile targetTile) {
        if (!GameSession.getCurrentPlayer().getEnergy().consumeEnergy(2)) {
            throw new IllegalStateException("You have not enough energy to use Scythe!");
        } else if (!targetTile.hasFeature(hasPlant.class)) {
            throw new IllegalStateException("You can not use this tool here!");
        } else {
            targetTile.getFeature(hasPlant.class).getCrop().harvest();
        }
    }

    @Override
    public void saveInInventory(int amount) {

    }
}
