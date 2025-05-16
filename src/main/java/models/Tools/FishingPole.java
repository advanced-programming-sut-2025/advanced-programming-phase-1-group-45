package models.Tools;

import models.MapElements.Tile.Tile;
import models.MapElements.crops.Plant.PlantInfo;
import models.Player;
import models.Tools.ToolLevel.FishingPoleLevel;

public class FishingPole extends Tool {
    private FishingPoleLevel level;
    private int farmingReachedLastLevel = 0;

    public FishingPole(FishingPoleLevel level) {
        super("FishingPole", level.getEnergy());
        this.level = level;
    }

    @Override
    public void useTool(Tile targetTile, Player player) {

    }

    public void decreaseEnergy() {

    }
}
