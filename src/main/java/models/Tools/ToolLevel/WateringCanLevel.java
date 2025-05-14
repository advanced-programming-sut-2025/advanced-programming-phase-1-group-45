package models.Tools.ToolLevel;

import models.Events.GameEventBus;

public enum WateringCanLevel {
    BASIC("Basic", 40, 5),
    COPPER("Copper", 55, 4),
    IRON("Iron", 70, 4),
    GOLD("Gold", 85, 2),
    IRIDIUM("Iridium", 100, 1);

    private final String name;
    private final int waterAmount;
    private final int energy;
    private int abilityReachedToLastLevel = 0;

    WateringCanLevel(String name, int waterAmount, int energy) {
        this.name = name;
        this.waterAmount = waterAmount;
        this.energy = energy;
        GameEventBus.INSTANCE.register(this);
    }

    public String getName() {
        return name;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getEnergy() {
        return energy;
    }

    public WateringCanLevel getNextLevel() {
        return switch (this) {
            case BASIC -> COPPER;
            case COPPER -> IRON;
            case IRON -> GOLD;
            case GOLD -> IRIDIUM;
            default -> null;
        };
    }
}
