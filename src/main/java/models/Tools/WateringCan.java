package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Farming;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.canWater;
import models.Player;
import models.Tools.ToolLevel.ToolLevel;
import models.Tools.ToolLevel.WateringCanLevel;

public class WateringCan extends UpgradeAbleTool {
    private WateringCanLevel level;
    private int waterAmount = level.getWaterAmount();
    private int farmingReachedLastLevel = 0;

    public WateringCan(WateringCanLevel level) {
        super("WateringCan", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }


    public void addWaterAmount(int amount) {
        waterAmount += amount;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    @Override
    public ToolLevel getLevel() {
        return ToolLevel.valueOf(level.getName());
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
        WateringCanLevel newCanLevel = level.getNextLevel();
        if (newCanLevel != null) {
            level = newCanLevel;
            WateringCan newCan = new WateringCan(level);
            GameEventBus.INSTANCE.post(new UpgradeToolEvent(this));
        } else {
            System.out.println("you reached to the last level");
        }
    }

    @Override
    public void useTool(Tile targetTile, Player player) {
        if (waterAmount == 0) {
            System.out.println("you have not enough water.");
            return;
        } else if (!(player.getEnergy().
                getCurrentEnergy() >= (level.getEnergy() - farmingReachedLastLevel))) {
            System.out.println("You have not enough Energy to use this tool.");
            return;
        }
        if (targetTile.hasFeature(canWater.class) && !targetTile.getFeature(canWater.class).isWateredToday()) {
            targetTile.getFeature(canWater.class).water();
            player.getEnergy().consumeEnergy(level.getEnergy() - farmingReachedLastLevel);
            waterAmount--;
        } else if (targetTile.hasFeature(canWater.class) &&
                targetTile.getFeature(canWater.class).isWateredToday()) {
           System.out.println("This tile is already watered today");
        } else {
           System.out.println("You can't use this tool in this tile");
        }
    }

}

