package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.MapElements.Stone;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileCreate;
import models.MapElements.Tile.TileFeatures.hasForaging;
import models.MapElements.Tile.TileFeatures.PlowSituation;
import models.MapElements.Tile.TileFeatures.hasForagingMinerals;
import models.MapElements.Tile.TileType;
import models.MapElements.crops.Plant.PlantInfo;
import models.Player;
import models.Tools.ToolLevel.ToolLevel;

public class Pickaxe extends UpgradeAbleTool {
    private ToolLevel level;
    private int miningReachedLastLevel = 0;

    public Pickaxe(ToolLevel level) {
        super("Pickaxe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }

    public void decreaseEnergy(Player player) {
        int energy = level.getEnergy() - miningReachedLastLevel;
        if (player.getEnergy().getCurrentEnergy() < energy) {
            System.out.println("You do not have enough energy to use this tool.");
            return;
        }
        player.getEnergy().consumeEnergy(energy);
    }

    @Override
    public void useTool(Tile targetTile, Player player) {
        decreaseEnergy(player);
           if(targetTile.hasFeature(PlowSituation.class)) {
            targetTile.getFeature(PlowSituation.class).unPlow();
        }
        if (targetTile.getTileType().equals(TileType.STONE)) {
                targetTile = TileCreate.create(TileType.PLAIN);
                targetTile.setSymbol('.');
                new Stone().saveInInventory(1, player);
                decreaseEnergy(player);
        } else if (targetTile.getTileType().equals(TileType.QUARRY) &&
        targetTile.hasFeature(hasForaging.class) &&
                (targetTile.getFeature(hasForaging.class) instanceof hasForagingMinerals)) {
            hasForagingMinerals hasMineral = (hasForagingMinerals) targetTile.getFeature(hasForaging.class);
            hasMineral.collectForagingElement(player);
        }
        else{
            System.out.println("You can not use this tool in this direction.");
            return;
        }
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

    @Override
    public void saveInInventory(int amount, Player player) {

    }
//
//    @Subscribe
//    public void miningReachedLastLevel(AbilityReachedMaxLevel event) {
//        if (event.ability() instanceof Mining) {
//            miningReachedLastLevel = 1;
//        }
//    }

}
