package com.proj.Model.inventoryItems.fertilizer;

import com.proj.Model.Inventory.InventoryItem; /** Callback interface for fertilizer selection */
public interface FertilizerSelectionListener {
    boolean onFertilizerSelected(InventoryItem fertilizer);
}
