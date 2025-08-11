package com.proj.Enums;

import com.badlogic.gdx.graphics.Texture;
import java.util.*;

public enum Shop {

    GENERAL_STORE(
        "Pierre",
        "Pierre",
        9,
        17,
        new ShopItem("Backpack Upgrade", "Increase inventory capacity", 2000, 1, null, null),
        new ShopItem("Parsnip Seeds", "Spring crop seeds", 20, -1, null, null),
        new ShopItem("Kale Seeds", "Fast-growing greens", 50, -1, null, null),
        new ShopItem("Garlic Seeds", "Pungent bulb seeds", 40, -1, null, null),
        new ShopItem("Rice", "Basic cooking ingredient", 100, -1, null, null),
        new ShopItem("Sugar", "Sweetening agent", 50, -1, null, null)
    ),

    BLACKSMITH(
        "Clint",
        "Clint",
        9,
        16,
        new ShopItem("Copper Ore", "A common ore that can be smelted into bars", 75, -1, null, null),
        new ShopItem("Iron Ore", "A fairly common ore that can be smelted into bars", 150, -1, null, null),
        new ShopItem("Coal", "A combustible rock useful for crafting and smelting", 150, -1, null, null),
        new ShopItem("Gold Ore", "A precious ore that can be smelted into bars", 400, -1, null, null),
        new ShopItem("Copper Tool", "Basic tool upgrade", 2000, 1,
            createMap(ShopItem.COPPER_BAR, 5), null), // Use ShopItem.COPPER_BAR
        new ShopItem("Steel Tool", "Improved tool upgrade", 5000, 1,
            createMap(ShopItem.IRON_BAR, 5), null),   // Use ShopItem.IRON_BAR
        new ShopItem("Gold Tool", "Advanced tool upgrade", 10000, 1,
            createMap(ShopItem.GOLD_BAR, 5), null),   // Use ShopItem.GOLD_BAR
        new ShopItem("Iridium Tool", "Ultimate tool upgrade", 25000, 1,
            createMap(ShopItem.IRIDIUM_BAR, 5), null), // Use ShopItem.IRIDIUM_BAR
        new ShopItem("Copper Trash Can", "Basic trash can", 1000, 1,
            createMap(ShopItem.COPPER_BAR, 5), null),
        new ShopItem("Steel Trash Can", "Improved trash can", 2500, 1,
            createMap(ShopItem.IRON_BAR, 5), null),
        new ShopItem("Gold Trash Can", "Luxury trash can", 5000, 1,
            createMap(ShopItem.GOLD_BAR, 5), null),
        new ShopItem("Iridium Trash Can", "Premium trash can", 12500, 1,
            createMap(ShopItem.IRIDIUM_BAR, 5), null)
    ),

    // ... (rest of your Shop enum, ensure createMap uses ShopItem constants where appropriate) ...
    CARPENTER(
        "Robin",
        "Robin",
        9,
        20,
        new ShopItem("Wood", "Basic building material", 50, -1, null, null),
        new ShopItem("Stone", "Versatile building stone", 60, -1, null, null),
        new ShopItem("Hardwood", "Premium building material", 150, -1, null, null),
        new ShopItem("Barn Kit", "Animal housing", 6000, 1,
            createMap(ShopItem.WOOD, 350, ShopItem.STONE, 150), null), // Use ShopItem.WOOD, ShopItem.STONE
        new ShopItem("Coop Kit", "Poultry housing", 4000, 1,
            createMap(ShopItem.WOOD, 300, ShopItem.STONE, 100), null), // Use ShopItem.WOOD, ShopItem.STONE
        new ShopItem("Silo", "Hay storage", 10000, 1,
            createMap(ShopItem.STONE, 100, ShopItem.CLAY, 10, ShopItem.COPPER_BAR, 5), null) // Use ShopItem constants
    ),

    FISH_SHOP(
        "Willy",
        "Willy",
        9,
        17,
        new ShopItem("Bamboo Pole", "Beginner fishing rod", 500, 1, null, null),
        new ShopItem("Fiberglass Rod", "Upgraded fishing rod", 1800, 1, null, null),
        new ShopItem("Iridium Rod", "Professional fishing rod", 7500, 1, null, null),
        new ShopItem("Bait", "Fishing accessory", 5, -1, null, null),
        new ShopItem("Tackle Box", "Fishing gear organizer", 2000, 1, null, null),
        new ShopItem("Crab Pot", "Passive fishing trap", 1500, 5,
            createMap(ShopItem.IRON_BAR, 2), null) // Use ShopItem.IRON_BAR
    ),

    RANCH(
        "Marnie",
        "Marnie",
        9,
        16,
        new ShopItem("Chicken", "White laying hen", 800, 5, null, null),
        new ShopItem("Duck", "Quacking waterfowl", 1200, 3, null, null),
        new ShopItem("Cow", "Milking cow", 1500, 3, null, null),
        new ShopItem("Goat", "Milk producer", 4000, 2, null, null),
        new ShopItem("Hay", "Animal feed", 50, -1, null, null),
        new ShopItem("Cheese Press", "Dairy processor", 3000, 1,
            createMap(ShopItem.WOOD, 50, ShopItem.STONE, 25), null) // Use ShopItem constants
    ),

    SALOON(
        "Gus",
        "Gus",
        12,
        24,
        new ShopItem("Stardrop Special", "Signature cocktail", 2000, 1, null, null),
        new ShopItem("Pizza", "Classic cheese pizza", 600, -1, null, null),
        new ShopItem("Salad", "Fresh garden salad", 220, -1, null, null),
        new ShopItem("Bread", "Freshly baked loaf", 120, -1, null, null),
        new ShopItem("Coffee", "Energy booster", 300, -1, null, null),
        new ShopItem("Recipe: Survival", "Forage cooking guide", 500, 1, null, null)
    );

    // Helper method to create immutable maps
    private static Map<ShopItem, Integer> createMap(Object... entries) {
        Map<ShopItem, Integer> map = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((ShopItem) entries[i], (Integer) entries[i+1]);
        }
        return Collections.unmodifiableMap(map);
    }

    private final String manager;
    private final int openHour;
    private final int closeHour;
    private final List<ShopItem> items;
    private final String displayName;

    Shop(String manager,String displayName, int openHour, int closeHour, ShopItem... items) {
        this.manager = manager;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.items = Collections.unmodifiableList(Arrays.asList(items));
        this.displayName = displayName;
    }

    public static class ShopItem {
        // Material definitions (ensure these are unique instances, perhaps a Map for global access)
        // Consider a separate registry for all possible items to avoid re-creating these
        public static final ShopItem COPPER_BAR = new ShopItem("Copper Bar", "A shiny copper bar.", 0, 0, null, null);
        public static final ShopItem IRON_BAR = new ShopItem("Iron Bar", "A sturdy iron bar.", 0, 0, null, null);
        public static final ShopItem GOLD_BAR = new ShopItem("Gold Bar", "A gleaming gold bar.", 0, 0, null, null);
        public static final ShopItem IRIDIUM_BAR = new ShopItem("Iridium Bar", "A rare iridescent bar.", 0, 0, null, null);
        public static final ShopItem WOOD = new ShopItem("Wood", "A piece of wood.", 0, 0, null, null);
        public static final ShopItem STONE = new ShopItem("Stone", "A common piece of stone.", 0, 0, null, null);
        public static final ShopItem CLAY = new ShopItem("Clay", "A lump of clay.", 0, 0, null, null);
        // Add other material items as needed
        public static final ShopItem COPPER_ORE = new ShopItem("Copper Ore", "A common ore.", 0, 0, null, null);
        public static final ShopItem IRON_ORE = new ShopItem("Iron Ore", "A fairly common ore.", 0, 0, null, null);
        public static final ShopItem COAL = new ShopItem("Coal", "A combustible rock.", 0, 0, null, null);
        public static final ShopItem GOLD_ORE = new ShopItem("Gold Ore", "A precious ore.", 0, 0, null, null);
        public static final ShopItem HARDWOOD = new ShopItem("Hardwood", "Premium wood.", 0, 0, null, null);
        public static final ShopItem HAY = new ShopItem("Hay", "Animal feed.", 0, 0, null, null);
        public static final ShopItem RICE = new ShopItem("Rice", "Basic cooking ingredient.", 0, 0, null, null);
        public static final ShopItem SUGAR = new ShopItem("Sugar", "Sweetening agent.", 0, 0, null, null);


        private final String name;
        private final String description;
        private final int price;
        private final int stockLimit;
        private final Map<ShopItem, Integer> requiredMaterials;
        private final Texture icon;

        public ShopItem(String name, String description, int price, int stockLimit,
                        Map<ShopItem, Integer> requiredMaterials, Texture icon) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockLimit = stockLimit;
            this.requiredMaterials = requiredMaterials != null ?
                Collections.unmodifiableMap(requiredMaterials) :
                Collections.emptyMap();
            this.icon = icon;
        }

        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getPrice() { return price; }
        public int getStockLimit() { return stockLimit; }
        public Map<ShopItem, Integer> getRequiredMaterials() { return requiredMaterials; }
        public Texture getIcon() { return icon; }

        // --- IMPORTANT: Override equals() and hashCode() ---
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShopItem shopItem = (ShopItem) o;
            return name.equals(shopItem.name); // Compare based on name
        }

        @Override
        public int hashCode() {
            return Objects.hash(name); // Hash based on name
        }
    }

    public String getManager() { return manager; }
    public int getOpenHour() { return openHour; }
    public int getCloseHour() { return closeHour; }
    public List<ShopItem> getItems() { return items; }
    @Override
    public String toString() {
        return displayName;
    }
}
