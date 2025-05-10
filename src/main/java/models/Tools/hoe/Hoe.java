package models.Tools.hoe;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Farming;
import models.Tools.Tool;
import models.Tools.ToolLevel;
import models.Tools.UpgradeAble;
import models.User;

public class Hoe extends Tool implements UpgradeAble {
    private ToolLevel level;
    private int farmingReachedToMaxLevel = 0;

    public Hoe(ToolLevel level) {
        super(level.getName(), level.getEnergy());
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
        User.decreaseEnergy(level.getEnergy() - farmingReachedToMaxLevel);
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
            Hoe newHoe = new Hoe(level);
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
