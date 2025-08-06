package com.proj.Model.Animal;

public enum ProductType {
    COW_MILK("Cow_Milk", 125),
    COW_LARGE_MILK("Cow_Large_Milk", 190),
    EGG("Egg", 50),
    LARGE_EGG("Large_Egg", 95),
    DUCK_EGG("Duck_Egg", 95),
    DUCK_FEATHER("Duck_Feather", 250),
    GOAT_MILK("Goat_Milk", 225),
    GOAT_LARGE_MILK("Goat_Large_Milk", 345),
    SHEEP_WOOL("Sheep_Wool", 340),
    RABBIT_WOOL("Rabbit_Wool", 340),
    TRUFFLE("Truffle", 625),
    DINOSAUR_EGG("Dinosaur_Egg", 350);

    private final String name;
    private final int price;

    ProductType(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
