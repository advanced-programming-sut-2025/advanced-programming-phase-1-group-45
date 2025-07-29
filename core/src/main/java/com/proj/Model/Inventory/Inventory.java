package com.proj.Model.Inventory;

import com.badlogic.gdx.Gdx;
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
    private boolean noToolSelected = true; // برای شروع بدون ابزار

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

        for (Integer slot : items.keySet()) {
            InventoryItem existingItem = items.get(slot);
            if (existingItem.canStack(item)) {
                existingItem.increaseQuantity(item.getQuantity());
                return true;
            }
        }

        for (int i = 0; i < capacity; i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);
                return true;
            }
        }
        return false;
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
        if (noToolSelected) {
            return null;
        }
        return items.get(selectedSlot);
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < capacity) {
            selectedSlot = slot;
            noToolSelected = (items.get(slot) == null);
            Gdx.app.log("Inventory", "Selected slot " + slot + ", is empty: " + noToolSelected);
        }
    }

    public boolean isNoToolSelected() {
        return noToolSelected;
    }

    public void setNoToolSelected(boolean noToolSelected) {
        this.noToolSelected = noToolSelected;
    }

    public void selectNoTool() {
        noToolSelected = true;
        Gdx.app.log("Inventory", "No tool selected");
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

