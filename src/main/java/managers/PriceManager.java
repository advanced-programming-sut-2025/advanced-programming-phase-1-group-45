package managers;

import java.util.Map;
import java.util.HashMap;

public class PriceManager {
    private static Map<String, Double> baseSellPrice = new HashMap<>();
    static {
        //stock
        baseSellPrice.put("Cooper Ore", 75.0);
        baseSellPrice.put("Iron Ore", 150.0);
        baseSellPrice.put("Gold Ore", 75.0);
        baseSellPrice.put("Coal", 150.0);
        //upgrade tools
        baseSellPrice.put("Copper Tool", 2000.0);
        baseSellPrice.put("Gold Tool", 10000.0);
        baseSellPrice.put("Iridium Tool", 25000.0);
        baseSellPrice.put("Copper Trash Can", 1000.0);
        baseSellPrice.put("Steel Trash Can", 2500.0);
        baseSellPrice.put("Gold Trash Can", 5000.0);
        baseSellPrice.put("Iridium Trash Can", 12500.0);
        //shop inventory
        baseSellPrice.put("Hay", 50.0);
        baseSellPrice.put("Milk Pail", 1000.0);
        baseSellPrice.put("Shears", 1000.0);
        //lives tock
        baseSellPrice.put("Chicken", 800.0);
        baseSellPrice.put("Cow", 1500.0);
        baseSellPrice.put("Goat", 4000.0);
        baseSellPrice.put("Duck", 1200.0);
        baseSellPrice.put("Sheep", 8000.0);
        baseSellPrice.put("Rabbit", 8000.0);
        baseSellPrice.put("Dinosaur", 14000.0);
        baseSellPrice.put("Pig", 16000.0);
        //Permanent Stock
        baseSellPrice.put("Beer", 400.0);
        baseSellPrice.put("Salad", 220.0);
        baseSellPrice.put("Bread", 120.0);
        baseSellPrice.put("Spaghetti", 240.0);
        baseSellPrice.put("Pizza", 600.0);
        baseSellPrice.put("Coffee", 300.0);
        baseSellPrice.put("Hashbrowns", 50.0);
        baseSellPrice.put("Omelet", 100.0);
        baseSellPrice.put("Pancakes", 100.0);
        baseSellPrice.put("Bread", 100.0);
        baseSellPrice.put("Tortilla", 100.0);
        baseSellPrice.put("Pizza", 150.0);
        baseSellPrice.put("Maki Roll", 300.0);
        baseSellPrice.put("Triple Shot Espresso", 5000.0);
        baseSellPrice.put("Cookie", 300.0);
        //permanent stock
        baseSellPrice.put("Wood",10.0);
        baseSellPrice.put("Stone", 20.0);
        //farm buildings
        baseSellPrice.put("Barn", 6000.0);
        baseSellPrice.put("Big Barn", 12000.0);
        baseSellPrice.put("Delux Barn", 25000.0);
        baseSellPrice.put("Coop", 4000.0);
        baseSellPrice.put("Big Coop", 10000.0);
        baseSellPrice.put("Delux Coop", 20000.0);
        baseSellPrice.put("Well", 1000.0);
        baseSellPrice.put("Shipping Bin", 250.0);
        baseSellPrice.put("Joja Cola", 75.0);
        baseSellPrice.put("Ancient Seed", 500.0);
        baseSellPrice.put("Grass Starter", 125.0);
        baseSellPrice.put("Sugar", 125.0);
        baseSellPrice.put("Wheat Flour", 125.0);
        baseSellPrice.put("Rice", 250.0);

// Spring Stock
        baseSellPrice.put("Parsnip Seeds", 25.0);
        baseSellPrice.put("Bean Starter", 75.0);
        baseSellPrice.put("Cauliflower Seeds", 100.0);
        baseSellPrice.put("Potato Seeds", 62.0);
        baseSellPrice.put("Strawberry Seeds", 100.0);
        baseSellPrice.put("Tulip Bulb", 25.0);
        baseSellPrice.put("Kale Seeds", 87.0);
        baseSellPrice.put("Coffee Beans", 200.0);
        baseSellPrice.put("Carrot Seeds", 5.0);
        baseSellPrice.put("Rhubarb Seeds", 100.0);
        baseSellPrice.put("Jazz Seeds", 37.0);

// Summer Stock
        baseSellPrice.put("Tomato Seeds", 62.0);
        baseSellPrice.put("Pepper Seeds", 50.0);
        baseSellPrice.put("Wheat Seeds", 12.0);
        baseSellPrice.put("Summer Squash Seeds", 10.0);
        baseSellPrice.put("Radish Seeds", 50.0);
        baseSellPrice.put("Melon Seeds", 100.0);
        baseSellPrice.put("Hops Starter", 75.0);
        baseSellPrice.put("Poppy Seeds", 125.0);
        baseSellPrice.put("Spangle Seeds", 62.0);
        baseSellPrice.put("Starfruit Seeds", 400.0);
        baseSellPrice.put("Sunflower Seeds", 125.0);

// Fall Stock
        baseSellPrice.put("Corn Seeds", 187.0);
        baseSellPrice.put("Eggplant Seeds", 25.0);
        baseSellPrice.put("Pumpkin Seeds", 125.0);
        baseSellPrice.put("Broccoli Seeds", 15.0);
        baseSellPrice.put("Amaranth Seeds", 87.0);
        baseSellPrice.put("Grape Starter", 75.0);
        baseSellPrice.put("Beet Seeds", 20.0);
        baseSellPrice.put("Yam Seeds", 75.0);
        baseSellPrice.put("Bok Choy Seeds", 62.0);
        baseSellPrice.put("Cranberry Seeds", 300.0);
        baseSellPrice.put("Fairy Seeds", 250.0);
        baseSellPrice.put("Rare Seed", 1000.0);

// Winter Stock
        baseSellPrice.put("Powdermelon Seeds", 20.0);
        baseSellPrice.put("Rice", 200.0);
        baseSellPrice.put("Wheat Flour", 100.0);
        baseSellPrice.put("Bouquet", 1000.0);
        baseSellPrice.put("Wedding Ring", 10000.0);
        baseSellPrice.put("Dehydrator (Recipe)", 10000.0);
        baseSellPrice.put("Grass Starter (Recipe)", 1000.0);
        baseSellPrice.put("Sugar", 100.0);
        baseSellPrice.put("Oil", 200.0);
        baseSellPrice.put("Vinegar", 200.0);
        baseSellPrice.put("Deluxe Retaining Soil", 150.0);
        baseSellPrice.put("Grass Starter", 100.0);
        baseSellPrice.put("Speed-Gro", 100.0);
        baseSellPrice.put("Apple Sapling", 4000.0);
        baseSellPrice.put("Apricot Sapling", 2000.0);
        baseSellPrice.put("Cherry Sapling", 3400.0);
        baseSellPrice.put("Orange Sapling", 4000.0);
        baseSellPrice.put("Peach Sapling", 6000.0);
        baseSellPrice.put("Pomegranate Sapling", 6000.0);
        baseSellPrice.put("Basic Retaining Soil", 100.0);
        baseSellPrice.put("Quality Retaining Soil", 150.0);
        // Year-Round Stock
        // Inventory Upgrades
        baseSellPrice.put("Large Pack", 2000.0);
        baseSellPrice.put("Deluxe Pack", 10000.0);

// Spring Seeds (in-season prices)
        baseSellPrice.put("Parsnip Seeds", 20.0);
        baseSellPrice.put("Bean Starter", 60.0);
        baseSellPrice.put("Cauliflower Seeds", 80.0);
        baseSellPrice.put("Potato Seeds", 50.0);
        baseSellPrice.put("Tulip Bulb", 20.0);
        baseSellPrice.put("Kale Seeds", 70.0);
        baseSellPrice.put("Jazz Seeds", 30.0);
        baseSellPrice.put("Garlic Seeds", 40.0);
        baseSellPrice.put("Rice Shoot", 40.0);

// Summer Seeds (in-season prices)
        baseSellPrice.put("Melon Seeds", 80.0);
        baseSellPrice.put("Tomato Seeds", 50.0);
        baseSellPrice.put("Blueberry Seeds", 80.0);
        baseSellPrice.put("Pepper Seeds", 40.0);
        baseSellPrice.put("Wheat Seeds", 10.0);
        baseSellPrice.put("Radish Seeds", 40.0);
        baseSellPrice.put("Poppy Seeds", 100.0);
        baseSellPrice.put("Spangle Seeds", 50.0);
        baseSellPrice.put("Hops Starter", 60.0);
        baseSellPrice.put("Corn Seeds", 150.0);
        baseSellPrice.put("Sunflower Seeds", 200.0);
        baseSellPrice.put("Red Cabbage Seeds", 100.0);

// Fall Seeds (in-season prices)
        baseSellPrice.put("Eggplant Seeds", 20.0);
        baseSellPrice.put("Pumpkin Seeds", 100.0);
        baseSellPrice.put("Bok Choy Seeds", 50.0);
        baseSellPrice.put("Yam Seeds", 60.0);
        baseSellPrice.put("Cranberry Seeds", 240.0);
        baseSellPrice.put("Fairy Seeds", 200.0);
        baseSellPrice.put("Amaranth Seeds", 70.0);
        baseSellPrice.put("Grape Starter", 60.0);
        baseSellPrice.put("Artichoke Seeds", 30.0);
        // Fishing Equipment and Recipes
        baseSellPrice.put("Fish Smoker (Recipe)", 10000.0);
        baseSellPrice.put("Trout Soup", 250.0);
        baseSellPrice.put("Bamboo Pole", 500.0);
        baseSellPrice.put("Training Rod", 25.0);
        baseSellPrice.put("Fiberglass Rod", 1800.0);
        baseSellPrice.put("Iridium Rod", 7500.0);

    }

    public static double getBasePrice(String product) {return baseSellPrice.get(product);}

}
