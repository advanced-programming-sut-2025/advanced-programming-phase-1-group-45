package com.proj.map;

public enum TileType {
    EMPTY("Empty"),
    GRASS("Grass"),
    FIBER("Fiber"),
    STONE("Stone"),
    WOOD("Wood"),
    CABIN("Cabin"),
    QUARRY("Quarry"),
    WATER("Water"),
    SHOP("Shop"),
    FORAGING_CROP("Foraging"),
    FORAGING_MINERAL("Foraging_Mineral");
    private final String type;
    TileType(String type) {
        this.type = type;
    }

    public static TileType findTypeByName(String name) {
        for (TileType tileType : TileType.values()) {
            if (tileType.type.equalsIgnoreCase(name)) {
                return tileType;
            }
        }
        return null;
    }
}
