package models.Tools;

import com.google.common.eventbus.Subscribe;
import models.Events.AbilityReachedMaxLevel;
import models.Events.GameEventBus;
import models.Mining;
import models.Tools.Pickaxe.PickaxeType;

public enum ToolLevel {
    BASIC("Basic", 5),
    COPPER("Copper", 4),
    IRON("Iron", 3),
    GOLD("Gold", 2),
    IRIDIUM("Iridium", 1);

    private final String name;
    private final int energy;
    private int abilityReachedToLastLevel = 0;

    ToolLevel(String name, int energy) {
        this.name = name;
        this.energy = energy;
        GameEventBus.INSTANCE.register(this);
    }

    public String getName() {
        return name;
    }

    public int getEnergy(){
        return energy;
    }

    public ToolLevel getNextLevel() {
        return switch (this) {
            case BASIC -> COPPER;
            case COPPER -> IRON;
            case IRON -> GOLD;
            case GOLD -> IRIDIUM;
            default -> null;
        };
    }


}
