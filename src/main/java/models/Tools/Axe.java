package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Foraging;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasForaging;
import models.MapElements.Tile.TileFeatures.hasForagingSeed;
import models.MapElements.Tile.TileFeatures.hasTree;
import models.Tools.ToolLevel.ToolLevel;

public class Axe extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int foragingReachedToMaxLevel = 0;

    public Axe(ToolLevel level) {
        super("Axe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }


    @Override
    public void useTool(Tile targetTile) {
        if (targetTile.hasFeature(hasTree.class) ||
                (targetTile.hasFeature(hasForaging.class) &&
                        (targetTile.getFeature(hasForaging.class) instanceof hasForagingSeed)))
        {
            targetTile.changeSymbol(Tile.PLAIN.getSymbol());
            targetTile.changeDescription(Tile.PLAIN.getDescription());
            System.out.println("The tree cut successfully.");
        } else{
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