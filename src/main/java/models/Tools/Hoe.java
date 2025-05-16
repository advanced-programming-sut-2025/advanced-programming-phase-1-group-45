package models.Tools;

import com.google.common.eventbus.Subscribe;
import managers.Progress.Farming.FarmingManager;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.PlowSituation;
import models.Player;
import models.Tools.ToolLevel.ToolLevel;

public class Hoe extends UpgradeAbleTool {
    private ToolLevel level;
    private int farmingReachedToMaxLevel = 0;

    public Hoe(ToolLevel level) {
        super("Hoe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }

    public void decreaseEnergy() {
//        int energy = level.getEnergy() - farmingReachedToMaxLevel;
//        if (User.getEnergy().getCurrentEnergy() < energy) {
//            throw new IllegalArgumentException("You do not have enough energy to use this tool.");
//        }
//        User.getEnergy().consumeEnergy(energy);
    }

    public void useTool(Tile targetTile, Player player) {
        player.getEnergy().consumeEnergy(level.getEnergy());
        if (targetTile.getTileType().getDescription().equalsIgnoreCase("Plain")
                && !targetTile.getFeature(PlowSituation.class).isPlowed()) {
            targetTile.getFeature(PlowSituation.class).plow();
        } else {
            System.out.println("You can not use this tool in this direction.");
            return;
        }
    }

    @Override
    public ToolLevel getLevel() {
        return level;
    }

    public ToolLevel getNewToolLevel() {
        return level.getNextLevel();
    }

    @Override
    public void upgrade() {
        ToolLevel newHoeLevel = level.getNextLevel();
        if (newHoeLevel != null) {
            level = newHoeLevel;
        } else {
            System.out.println("you reached to the last level");
        }
    }

    @Subscribe
    public void onReachedMaxLevel(AbilityReachedMaxLevel event) {
        if (event.ability() instanceof FarmingManager) {
            farmingReachedToMaxLevel = 1;
        }
    }
}