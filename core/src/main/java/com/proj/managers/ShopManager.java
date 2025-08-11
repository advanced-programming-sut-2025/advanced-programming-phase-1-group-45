package com.proj.managers;

import com.badlogic.gdx.Gdx;
import com.proj.Enums.Shop;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Player;

import java.util.Arrays;
import java.util.List;

public class ShopManager {
    private final Player player;
    private final InventoryManager inventoryManager;

    public ShopManager(Player player, InventoryManager inventoryManager) {
        this.player = player;
        this.inventoryManager = inventoryManager;
    }

    public List<String> getItemsForSale() {
        return Arrays.asList(
            "Wheat Seeds",
            "Tomato Seeds",
            "Fertilizer",
            "Copper Hoe",
            "Iron Pickaxe"
        );
    }

    /*public boolean buyItem(String itemName, int quantity) {
        int price = getPriceForItem(itemName);
        if (price <= 0 || quantity <= 0) return false;

        int totalCost = price * quantity;
        if (player.getMoney() < totalCost) return false;

        player.addMoney(-totalCost);
        inventoryManager.addItem(itemName, quantity);
        return true;
    }*/
    /*public boolean buyItem(String itemName, int quantity) {
        // Use PriceManager instead of hardcoded prices
        Double price = PriceManager.getPrice(itemName);
        if (price == null) {
            Gdx.app.error("ShopManager", "Price not found for: " + itemName);
            return false;
        }

        int totalCost = (int) (price * quantity);
        Gdx.app.log("ShopManager",
            "Buying " + quantity + " " + itemName +
                " for " + totalCost + "g (Player has: " + player.getMoney() + "g)"
        );

        if (player.getMoney() < totalCost) {
            Gdx.app.log("ShopManager", "Not enough money!");
            return false;
        }

        player.addMoney(-totalCost);
        inventoryManager.addItem(itemName, quantity);
        return true;
    }*/
    public boolean buyItem(Shop shop, String itemName, int quantity) {
        Shop.ShopItem shopItem = findShopItem(shop, itemName);
        if (shopItem == null) return false;

        Double price = PriceManager.getPrice(itemName);
        if (price == null) return false;

        int totalCost = (int) (price * quantity);
        if (player.getMoney() < totalCost) return false;

        // Check stock if applicable
        if (shopItem.getStockLimit() > 0) {
            // Implement stock tracking logic here
        }

        player.addMoney(-totalCost);
        inventoryManager.addItem(itemName, quantity);
        return true;
    }

    public int getPriceForItem(String itemName) {
        switch (itemName) {
            case "Wheat Seeds": return 20;
            case "Tomato Seeds": return 30;
            case "Fertilizer": return 50;
            case "Copper Hoe": return 500;
            case "Iron Pickaxe": return 1000;
            default: return 0;
        }
    }

    public boolean sellItem(String itemName, int quantity) {
        // TODO: Implement selling, check inventory, add money etc.
        return false;
    }

    private Shop.ShopItem findShopItem(Shop shop, String itemName) {
        return shop.getItems().stream()
            .filter(item -> item.getName().equals(itemName))
            .findFirst()
            .orElse(null);
    }
}
