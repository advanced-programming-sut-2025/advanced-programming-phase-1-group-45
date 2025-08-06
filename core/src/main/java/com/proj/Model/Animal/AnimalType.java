package com.proj.Model.Animal;

public enum AnimalType {
    COW("cow", 1500, 1),
    CHICKEN("chicken", 800, 1),
    SHEEP("sheep", 1200, 3),
    GOAT("goat", 1300, 2),
    PIG("pig", 1600, 1),
    DUCK("duck", 1100, 2),
    RABBIT("rabbit", 1000, 4),
    DINOSAUR("dinosaur", 5000, 7);

    private final String type;
    private final int price;
    private final int period;

    AnimalType(String type, int price, int period) {
        this.type = type;
        this.price = price;
        this.period = period;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getPeriod() {
        return period;
    }
}
