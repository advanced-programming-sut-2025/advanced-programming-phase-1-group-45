package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Enums.Tile;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Mining;
import models.User;
import models.crops.Stone;

public class Pickaxe extends Tool implements UpgradeAbleTool {
    private ToolLevel level;
    private int miningReachedLastLevel = 0;

    public Pickaxe(ToolLevel level) {
        super("Pickaxe", level.getEnergy());
        this.level = level;
        GameEventBus.INSTANCE.register(this);
    }

    @Override
    public void decreaseEnergy() {
        int energy = level.getEnergy() - miningReachedLastLevel;
        if (User.getEnergy().getCurrentEnergy() < energy) {
            throw new IllegalArgumentException("You do not have enough energy to use this tool.");
        }
        User.getEnergy().consumeEnergy(energy);
    }

    @Override
    public void useTool(Tile targetTile) {
        checkingTargetTile(targetTile);
        if ((targetTile.getItem() instanceof Stone)&&

                || targetTile.isTilled()) {
            targetTile.tillThisTileWithHoe();
        } else {
            throw new IllegalArgumentException("You can not use this tool in this direction.");
        }
    }
    protected void checkingTargetTile(Tile targetTile) {
        if ((targetTile.getItem() instanceof Stone)){
            Stone stone = (Stone) targetTile.getItem();

        }
        else if(targetTile.isTilled()){
            targetTile.untilThisTileWithPickaxe();
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

    @Subscribe
    public void miningReachedLastLevel(AbilityReachedMaxLevel event) {
        if (event.ability() instanceof Mining) {
            miningReachedLastLevel = 1;
        }
    }
}
