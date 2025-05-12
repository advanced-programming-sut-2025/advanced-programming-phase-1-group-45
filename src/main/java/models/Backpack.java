package models;

import java.util.HashMap;
import java.util.Map;

public class Backpack {
    private Map<String, Integer> items;
    private int capacity;
    private String backpackType;
    private String trashCanType;

    public Backpack() {
        this.items = new HashMap<>();
        this.capacity = 12;
        this.backpackType = "Basic Backpack";
        this.trashCanType = "Basic Trash Can";
    }


    public void showInventory() {
        System.out.println("=== INVENTORY (" + items.size() + "/" + capacity + ") ===");
        if (items.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }


    public boolean addItem(String itemName, int count) {
        if (items.containsKey(itemName)) {
            items.put(itemName, items.get(itemName) + count);
            return true;
        } else {
            if (items.size() >= capacity) {
                System.out.println("Inventory is full!");
                return false;
            }
            items.put(itemName, count);
            return true;
        }
    }


    public boolean removeItem(String itemName, int count) {
        if (!items.containsKey(itemName)) {
            System.out.println("Item not found in inventory!");
            return false;
        }

        int currentCount = items.get(itemName);
        if (count >= currentCount) {
            items.remove(itemName);
        } else {
            items.put(itemName, currentCount - count);
        }
        return true;
    }


    public int trashItem(String itemName, int count) {
        if (!items.containsKey(itemName)) {
            System.out.println("Item not found in inventory!");
            return 0;
        }

        int currentCount = items.get(itemName);
        int actualCount = (count > currentCount || count <= 0) ? currentCount : count;


        int returnValue = 0;
        int itemValue = getItemValue(itemName);

        switch (trashCanType) {
            case "Copper Trash Can":
                returnValue = (int)(itemValue * 0.15 * actualCount);
                break;
            case "Iron Trash Can":
                returnValue = (int)(itemValue * 0.30 * actualCount);
                break;
            case "Gold Trash Can":
                returnValue = (int)(itemValue * 0.45 * actualCount);
                break;
            case "Iridium Trash Can":
                returnValue = (int)(itemValue * 0.60 * actualCount);
                break;
            default:
                returnValue = 0;
        }


        if (actualCount >= currentCount) {
            items.remove(itemName);
        } else {
            items.put(itemName, currentCount - actualCount);
        }

        return returnValue;
    }


    public void upgradeBackpack(String newType) {
        this.backpackType = newType;
        switch (newType) {
            case "Large Backpack":
                this.capacity = 24;
                break;
            case "Deluxe Backpack":
                this.capacity = Integer.MAX_VALUE;
                break;
        }
        System.out.println("Backpack upgraded to " + newType + "!");
    }


    public void upgradeTrashCan(String newType) {
        this.trashCanType = newType;
        System.out.println("Trash can upgraded to " + newType + "!");
    }

    public boolean hasItem(String itemName, int count) {
        return items.containsKey(itemName) && items.get(itemName) >= count;
    }

    public int getItemCount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }


    private int getItemValue(String itemName) {

        return 100;
    }
}
