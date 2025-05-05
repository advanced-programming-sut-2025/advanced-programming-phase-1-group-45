package models.Tools;

import models.Item;

import java.util.Map;

public class Backpack extends Tool {
    private int capacity;
    private Map<Item, Integer> items;

    public Backpack(int capacity) {
        this.capacity = capacity;
    }

    public void addItemAmount(Item item, int amount) {
        if (items.containsKey(item)) {
            items.put(item, items.get(item) + amount);
        } else {
            items.put(item, amount);
        }
    }

    public boolean removeItem(Item item, int amount) {
        int current = items.getOrDefault(item, 0);
        if(current >= amount) {
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

}
