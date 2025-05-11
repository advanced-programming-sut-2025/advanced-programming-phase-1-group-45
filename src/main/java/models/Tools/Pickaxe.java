package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Mining;
import models.User;

public class Pickaxe extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int miningReachedLastLevel = 0;

    public Pickaxe(ToolLevel level) {
        super("Pickaxe", level.getEnergy());
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
        User.decreaseEnergy(Math.max(level.getEnergy() - miningReachedLastLevel, 0));
    }

    @Override
    public ToolLevel getLevel() {
        return level;
    }

    @Override
    public void upgrade() {
        ToolLevel newPickaxeLevel = level.getNextLevel();
        if (newPickaxeLevel != null) {
            level = newPickaxeLevel;
            GameEventBus.INSTANCE.post(new UpgradeToolEvent(this));
        } else {
            System.out.println("you reached to the last level");
        }
    }

    @Subscribe
    public void miningReachedLastLevel(AbilityReachedMaxLevel event) {
        if (event.ability() instanceof Mining) {
            miningReachedLastLevel = 1;
        }
    }
}
