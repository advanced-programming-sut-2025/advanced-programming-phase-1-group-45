package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Enums.Tile;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Farming;
import models.User;

public class Hoe extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int farmingReachedToMaxLevel = 0;

    public Hoe(ToolLevel level) {
        super("Hoe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }

    @Override
    public void decreaseEnergy() {
//        int energy = level.getEnergy() - farmingReachedToMaxLevel;
//        if (User.getEnergy().getCurrentEnergy() < energy) {
//            throw new IllegalArgumentException("You do not have enough energy to use this tool.");
//        }
//        User.getEnergy().consumeEnergy(energy);
    }

    @Override
    public void useTool(Tile targetTile) {
        if (targetTile.getDescription().equalsIgnoreCase("Plain")
                && !targetTile.isTilled()) {
            targetTile.tillThisTileWithHoe();
        } else {
            throw new IllegalArgumentException("You can not use this tool in this direction.");
        }
    }

    @Override
    public ToolLevel getLevel() {
        return level;
    }

    @Override
    public void upgrade() {
        ToolLevel newHoeLevel = level.getNextLevel();
        if (newHoeLevel != null) {
            level = newHoeLevel;
            GameEventBus.INSTANCE.post(new UpgradeToolEvent(this));
        } else {
            System.out.println("you reached to the last level");
        }
    }

    @Subscribe
    public void onReachedMaxLevel(AbilityReachedMaxLevel event) {
        if (event.ability() instanceof Farming) {
            farmingReachedToMaxLevel = 1;
        }
    }
}
