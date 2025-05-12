package models.Enums;
import java.util.List;
import java.util.Map;

public enum Shop {
    BLACKSMITH(
            "Clint",
            9,
            16,
            new ShopItem("Copper Ore", "A common ore that can be smelted into bars", 75, -1, null),
            new ShopItem("Iron Ore", "A fairly common ore that can be smelted into bars", 150, -1, null),
            new ShopItem("Coal", "A combustible rock useful for crafting and smelting", 150, -1, null),
            new ShopItem("Gold Ore", "A precious ore that can be smelted into bars", 400, -1, null),
            new ShopItem("Copper Tool", "Basic tool upgrade", 2000, 1, Map.of(ShopItem.COPPER_BAR, 5)),
            new ShopItem("Steel Tool", "Improved tool upgrade", 5000, 1, Map.of(ShopItem.IRON_BAR, 5)),
            new ShopItem("Gold Tool", "Advanced tool upgrade", 10000, 1, Map.of(ShopItem.GOLD_BAR, 5)),
            new ShopItem("Iridium Tool", "Ultimate tool upgrade", 25000, 1, Map.of(ShopItem.IRIDIUM_BAR, 5)),
            new ShopItem("Copper Trash Can", "Basic trash can", 1000, 1, Map.of(ShopItem.COPPER_BAR, 5)),
            new ShopItem("Steel Trash Can", "Improved trash can", 2500, 1, Map.of(ShopItem.IRON_BAR, 5)),
            new ShopItem("Gold Trash Can", "Luxury trash can", 5000, 1, Map.of(ShopItem.GOLD_BAR, 5)),
            new ShopItem("Iridium Trash Can", "Premium trash can", 12500, 1, Map.of(ShopItem.IRIDIUM_BAR, 5))
    ),

    JOJA_MART(
            "Morris",
            9,
            23,
            new ShopItem("Blueberry Seeds", "Seasonal fruit seeds", 80, -1, null),
            new ShopItem("Cranberry Seeds", "Autumn crop seeds", 120, -1, null),
            new ShopItem("Melon Seeds", "Summer fruit seeds", 80, -1, null),
            new ShopItem("Quality Fertilizer", "Crop quality booster", 150, -1, null),
            new ShopItem("Speed-Gro", "Growth accelerator", 100, -1, null),
            new ShopItem("Sprinkler", "Automatic watering system", 2000, 5, null)
    ),

    GENERAL_STORE(
            "Pierre",
            9,
            17,
            new ShopItem("Backpack Upgrade", "Increase inventory capacity", 2000, 1, null),
            new ShopItem("Parsnip Seeds", "Spring crop seeds", 20, -1, null),
            new ShopItem("Kale Seeds", "Fast-growing greens", 50, -1, null),
            new ShopItem("Garlic Seeds", "Pungent bulb seeds", 40, -1, null),
            new ShopItem("Rice", "Basic cooking ingredient", 100, -1, null),
            new ShopItem("Sugar", "Sweetening agent", 50, -1, null)
    ),

    CARPENTER(
            "Robin",
            9,
            20,
            new ShopItem("Wood", "Basic building material", 50, -1, null),
            new ShopItem("Stone", "Versatile building stone", 60, -1, null),
            new ShopItem("Hardwood", "Premium building material", 150, -1, null),
            new ShopItem("Barn Kit", "Animal housing", 6000, 1, Map.of(ShopItem.WOOD, 350, ShopItem.STONE, 150)),
            new ShopItem("Coop Kit", "Poultry housing", 4000, 1, Map.of(ShopItem.WOOD, 300, ShopItem.STONE, 100)),
            new ShopItem("Silo", "Hay storage", 10000, 1, Map.of(ShopItem.STONE, 100, ShopItem.CLAY, 10, ShopItem.COPPER_BAR, 5))
    ),

    FISH_SHOP(
            "Willy",
            9,
            17,
            new ShopItem("Bamboo Pole", "Beginner fishing rod", 500, 1, null),
            new ShopItem("Fiberglass Rod", "Upgraded fishing rod", 1800, 1, null),
            new ShopItem("Iridium Rod", "Professional fishing rod", 7500, 1, null),
            new ShopItem("Bait", "Fishing accessory", 5, -1, null),
            new ShopItem("Tackle Box", "Fishing gear organizer", 2000, 1, null),
            new ShopItem("Crab Pot", "Passive fishing trap", 1500, 5, Map.of(ShopItem.IRON_BAR, 2))
    ),

    RANCH(
            "Marnie",
            9,
            16,
            new ShopItem("Chicken", "White laying hen", 800, 5, null),
            new ShopItem("Duck", "Quacking waterfowl", 1200, 3, null),
            new ShopItem("Cow", "Milking cow", 1500, 3, null),
            new ShopItem("Goat", "Milk producer", 4000, 2, null),
            new ShopItem("Hay", "Animal feed", 50, -1, null),
            new ShopItem("Cheese Press", "Dairy processor", 3000, 1, Map.of(ShopItem.WOOD, 50, ShopItem.STONE, 25))
    ),

    SALOON(
            "Gus",
            12,
            24,
            new ShopItem("Stardrop Special", "Signature cocktail", 2000, 1, null),
            new ShopItem("Pizza", "Classic cheese pizza", 600, -1, null),
            new ShopItem("Salad", "Fresh garden salad", 220, -1, null),
            new ShopItem("Bread", "Freshly baked loaf", 120, -1, null),
            new ShopItem("Coffee", "Energy booster", 300, -1, null),
            new ShopItem("Recipe: Survival", "Forage cooking guide", 500, 1, null)
    );

    private final String manager;
    private final int openHour;
    private final int closeHour;
    private final List<ShopItem> items;
   // private Map<String, itemInfo> inventory;

    Shop(String manager, int openHour, int closeHour, ShopItem... items) {
        this.manager = manager;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.items = List.of(items);
        //this.inventory = inventory;
    }

    /*public Map<String, itemInfo> getInventory(){return inventory;}
    public static class itemInfo {
        public final Double price;
        public final Double dailyStock;

        public itemInfo(Double price, Double dailyStock) {
            this.price = price;
            this.dailyStock = dailyStock;
        }
    }*/
    public static class ShopItem {
        // مواد اولیه پایه
        public static final ShopItem COPPER_BAR = new ShopItem("Copper Bar", "", 0, 0, null);
        public static final ShopItem IRON_BAR = new ShopItem("Iron Bar", "", 0, 0, null);
        public static final ShopItem GOLD_BAR = new ShopItem("Gold Bar", "", 0, 0, null);
        public static final ShopItem IRIDIUM_BAR = new ShopItem("Iridium Bar", "", 0, 0, null);
        public static final ShopItem WOOD = new ShopItem("Wood", "", 0, 0, null);
        public static final ShopItem STONE = new ShopItem("Stone", "", 0, 0, null);
        public static final ShopItem CLAY = new ShopItem("Clay", "", 0, 0, null);

        private final String name;
        private final String description;
        private final int price;
        private final int stockLimit;

        private final Map<ShopItem, Integer> requiredMaterials;

        public ShopItem(String name, String description, int price, int stockLimit,
                        Map<ShopItem, Integer> requiredMaterials) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockLimit = stockLimit;
            this.requiredMaterials = requiredMaterials != null ? requiredMaterials : Map.of();
        }

        // Getter ها
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public int getStockLimit() { return stockLimit; }
        public Map<ShopItem, Integer> getRequiredMaterials() { return requiredMaterials; }
    }

    // Getter ها
    public String getManager() { return manager; }
    //public static int getOpenHour() { return openHour; }
    public int getCloseHour() { return closeHour; }
    public List<ShopItem> getItems() { return items; }
}