package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Farming;
import models.User;

public class WateringCan extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int farmingReachedLastLevel = 0;

    public WateringCan(ToolLevel level) {
        super("WateringCan", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }

    @Override
    public void useTool() {
        if (User.getEnergy < level.getEnergy()) {
            throw new IllegalArgumentException("You do not have enough energy to use this tool.");
        }
        //TODO
        //shokm zadan
        User.decreaseEnergy(Math.max(level.getEnergy() - farmingReachedLastLevel, 0));
    }

    @Override
    public ToolLevel getLevel() {
        return level;
    }


    @Subscribe
    public void miningReachedLastLevel(AbilityReachedMaxLevel event) {
        if (event.ability() instanceof Farming) {
            farmingReachedLastLevel = 1;
        }
    }

    public int getTileNumber(ToolLevel level) {
        return switch (level) {
            case BASIC -> 40;
            case COPPER -> 55;
            case IRON -> 70;
            case GOLD -> 85;
            case IRIDIUM -> 100;
        };
    }


    @Override
    public void upgrade() {
        ToolLevel newCanLevel = level.getNextLevel();
        if (newCanLevel != null) {
            level = newCanLevel;
            WateringCan newCan = new WateringCan(level);
            GameEventBus.INSTANCE.post(new UpgradeToolEvent(this));
        } else {
            System.out.println("you reached to the last level");
        }
    }

}
