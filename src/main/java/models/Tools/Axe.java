package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Enums.Tile;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Foraging;
import models.User;

public class Axe extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int foragingReachedToMaxLevel = 0;

    public Axe(ToolLevel level) {
        super("Axe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }

    @Override
    public void decreaseEnergy() {
        int energy = level.getEnergy() - foragingReachedToMaxLevel;
        if (User.getEnergy < energy) {
            throw new IllegalArgumentException("You do not have enough energy to use this tool.");
        }
        User.decreaseEnergy(energy);
    }

    @Override
    public void useTool(Tile targetTile) {
        if (targetTile.getDescription().equalsIgnoreCase("tree")) {
            targetTile.changeSymbol(Tile.PLAIN.getSymbol());
            targetTile.changeDescription(Tile.PLAIN.getDescription());
            System.out.println("The tree cut successfully.");
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
            foragingReachedToMaxLevel = 1;
        }
    }
}
