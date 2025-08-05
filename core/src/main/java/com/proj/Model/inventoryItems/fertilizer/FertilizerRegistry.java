package com.proj.Model.inventoryItems.fertilizer;

import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;

public class FertilizerRegistry {
    private static FertilizerRegistry instance;
    private final Array<InventoryItem> fertilizers = new Array<>();

    private FertilizerRegistry() {
        registerFertilizers();
    }

    public static FertilizerRegistry getInstance() {
        if (instance == null) {
            instance = new FertilizerRegistry();
        }
        return instance;
    }

    private void registerFertilizers() {
        fertilizers.add(new BasicFertilizer());
        fertilizers.add(new DeluxeFertilizer());
    }

    public Array<InventoryItem> getFertilizers() {
        return fertilizers;
    }

    public InventoryItem get(String name) {
        for (InventoryItem fertilizer : fertilizers) {
            if (fertilizer.getName().equals(name)) {
                return fertilizer;
            }
        }
        return null;
    }
}
