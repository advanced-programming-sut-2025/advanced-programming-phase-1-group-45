package models.Tools;

import models.GameSession;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasPlant;
import models.Player;

public class Scythe extends Tool {

    public Scythe(int energy) {
        super("Scythe", 2);
    }


    @Override
    public void useTool(Tile targetTile, Player player) {
        player.getEnergy().consumeEnergy(2);
        if (!player.getEnergy().consumeEnergy(2)) {
            System.out.println("You have not enough energy to use Scythe!");
        } else if (!targetTile.hasFeature(hasPlant.class)) {
           System.out.println("You can not use this tool here!");
        } else {
            targetTile.getFeature(hasPlant.class).getCrop().harvest();
        }
    }

    public void saveInInventory(int amount) {

    }
}
