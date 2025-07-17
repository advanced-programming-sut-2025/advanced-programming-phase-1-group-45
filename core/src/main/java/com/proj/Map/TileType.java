package com.proj.Enum;

import com.proj.Map.Tile;

public enum TileType {
    DIRT("Dirt"),
    GRASS("Grass"),
    STONE("Stone"),
    WOOD("Wood"),
    CABIN("Cabin"),
    QUARRY("Quarry"),
    WATER("Water"),
    SHOP("Shop");
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
