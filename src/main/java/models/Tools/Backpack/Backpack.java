package models.Tools.Backpack;

import models.Item;

import java.util.HashMap;
import java.util.Map;

public class Backpack {

    private BackpackType backpackType;
    private Map<Item, Integer> items;

    public Backpack(int capacity) {
        this.backpackType = BackpackType.BASIC;
        this.items = new HashMap<>();
    }

    public void addItemAmount(Item item, int amount) {
        if (backpackType.canAddItem(items.size())) {
            if (items.containsKey(item)) {
                items.put(item, items.get(item) + amount);
            } else {
                items.put(item, amount);
            }
        } else {
            throw new IllegalArgumentException("Backpack is full");
        }
    }

    public boolean removeItem(Item item, int amount) {
        int current = items.getOrDefault(item, 0);
        if (current >= amount) {
            items.put(item, current - amount);
            return true;
        }
        return false;
    }

    public boolean hasItem(String item) {
        return getNumberOfAnItem(item) > 0;
    }

    public int getNumberOfAnItem(String itemName) {
        for (Item item : items.keySet()) {
            if (item.getItemName().equals(itemName)) {
                return items.get(item);
            }
        }
        return 0;
    }

    public String getName() {
        return backpackType.getName();
    }

    public int getCapacity() {
        return backpackType.getCapacity();
    }

    public void upgradeBackpack() {
        this.backpackType = backpackType.upgrade();
    }

}
