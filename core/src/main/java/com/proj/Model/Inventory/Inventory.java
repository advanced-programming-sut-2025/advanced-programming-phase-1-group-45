package com.proj.Model.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private static final int DEFAULT_CAPACITY = 12;
    private int capacity;
    private Map<Integer, InventoryItem> items;
    private int selectedSlot = 0;
    private boolean isVisible = false;

    public Inventory() {
        this.capacity = DEFAULT_CAPACITY;
        this.items = new HashMap<>();
    }

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new HashMap<>();
    }

    public boolean addItem(InventoryItem item) {
        if (item == null) return false;

        // Check if similar item exists with space
        for (Integer slot : items.keySet()) {
            InventoryItem existingItem = items.get(slot);
            if (existingItem.canStack(item)) {
                existingItem.increaseQuantity(item.getQuantity());
                return true;
            }
        }

        // Find empty slot
        for (int i = 0; i < capacity; i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);
                return true;
            }
        }
        return false; // Inventory is full
    }

    public InventoryItem getItem(int slot) {
        return items.get(slot);
    }

    public InventoryItem removeItem(int slot) {
        return items.remove(slot);
    }

    public boolean hasItem(String itemId) {
        for (InventoryItem item : items.values()) {
            if (item.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public InventoryItem getSelectedItem() {
        return items.get(selectedSlot);
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < capacity) {
            selectedSlot = slot;
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<InventoryItem> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public void clear() {
        items.clear();
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void update(float delta) {
        // Update all tools in inventory
        for (InventoryItem item : items.values()) {
            if (item instanceof Tool) {
                ((Tool) item).update(delta);
            }
        }
    }

    public Map<Integer, InventoryItem> getItems() {
        return items;
    }
}
