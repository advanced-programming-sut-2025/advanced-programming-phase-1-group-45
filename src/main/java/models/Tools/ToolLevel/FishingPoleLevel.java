package models.Tools.ToolLevel;

import models.Events.GameEventBus;

public enum FishingPoleLevel {
    BASIC("Basic", 25, 8),
    BAMBOO("Bamboo", 500, 8),
    FIBERGLASS("Fiberglass", 1800, 6),
    IRIDIUM("Iridium", 7500, 4);

    private final String name;
    private final int price;
    private final int energy;
    private int abilityReachedToLastLevel = 0;

    FishingPoleLevel(String name, int price, int energy) {
        this.name = name;
        this.price = price;
        this.energy= energy;
        GameEventBus.INSTANCE.register(this);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
    public int getEnergy() {
        return energy;
    }

    public FishingPoleLevel getNextLevel() {
        return switch (this) {
            case BASIC -> BAMBOO;
            case BAMBOO -> FIBERGLASS;
            case FIBERGLASS -> IRIDIUM;
            default -> null;
        };
    }
}
