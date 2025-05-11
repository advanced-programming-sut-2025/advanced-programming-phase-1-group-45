package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Foraging;
import models.User;

public class Axe extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int foragingReachedToMaxLevel = 0;

    public Axe(ToolLevel level) {
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
        User.decreaseEnergy(level.getEnergy() - foragingReachedToMaxLevel);
    }

    @Override
    public ToolLevel getLevel() {
        return level;
    }

    @Override
    public void upgrade() {
        ToolLevel newAxeLevel = level.getNextLevel();
        if (newAxeLevel != null) {
            level = newAxeLevel;
            GameEventBus.INSTANCE.post(new UpgradeToolEvent(this));
        } else {
            System.out.println("you reached to the last level");
        }
    }

    @Subscribe
    public void onReachedMaxLevel(AbilityReachedMaxLevel event) {
        if (event.ability() instanceof Foraging) {
            foragingReachedToMaxLevel= 1;
        }
    }
}
