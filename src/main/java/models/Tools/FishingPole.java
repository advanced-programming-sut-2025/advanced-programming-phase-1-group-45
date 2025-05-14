package models.Tools;

import models.MapElements.Tile.Tile;
import models.Tools.ToolLevel.FishingPoleLevel;

public class FishingPole extends Tool {
    private FishingPoleLevel level;
    private int farmingReachedLastLevel = 0;

    public FishingPole(FishingPoleLevel level) {
        super("FishingPole", level.getEnergy());
        this.level = level;
    }

    @Override
    public void useTool(Tile targetTile) {

    }

    @Override
    public void decreaseEnergy() {

    }
}
