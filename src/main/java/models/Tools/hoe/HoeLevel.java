package models.Tools.hoe;

public enum HoeLevel {
    BASIC("Basic", 5),
    COPPER("Copper", 4),
    GOLD("Gold", 2),
    IRIDIUM("Iridium", 1),
    IRON("Iron", 3);
    private final String name;
    private final int energy;
    HoeLevel(String name, int energy) {
        this.name = name;
        this.energy = energy;
    }
    public String getName() {
        return name;
    }
    public int getEnergy() {
        return energy;
    }
}
