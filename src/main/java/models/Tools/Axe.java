package models.Tools;

import com.google.common.eventbus.Subscribe;
import managers.Progress.ForagingManager;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasForaging;
import models.MapElements.Tile.TileFeatures.hasForagingSeed;
import models.MapElements.Tile.TileFeatures.hasTree;
import models.MapElements.Tile.TileType;
import models.Player;
import models.Tools.ToolLevel.ToolLevel;

public class Axe extends UpgradeAbleTool {
    private ToolLevel level;
    private int foragingReachedToMaxLevel = 0;

    public Axe(ToolLevel level) {
        super("Axe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }


    @Override
    public void useTool(Tile targetTile, Player player) {
        player.getEnergy().consumeEnergy(level.getEnergy());
        if (targetTile.hasFeature(hasTree.class) ||
                (targetTile.hasFeature(hasForaging.class) &&
                        (targetTile.getFeature(hasForaging.class) instanceof hasForagingSeed))) {
            hasTree hasTree = targetTile.getFeature(hasTree.class);
            if (hasTree != null) {
                hasTree.chopTree(player);
            }
            else {
                hasForagingSeed hasForagingSeed = (hasForagingSeed) targetTile.getFeature(hasForaging.class);
                hasForagingSeed.chopTree(player);
            }
            targetTile.setTileType(TileType.PLAIN);
            System.out.println("The tree cut successfully.");
        } else {
            System.out.println("You can not use this tool in this direction.");
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
        if (event.ability() instanceof ForagingManager) {
            foragingReachedToMaxLevel = 1;
        }
    }
}