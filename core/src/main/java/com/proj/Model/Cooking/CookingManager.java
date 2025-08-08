// CookingManager.java
package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.proj.Player; // اضافه کردن برای کسر انرژی
import com.proj.Model.Cooking.CookingRecipe;
import com.proj.Model.Inventory.Inventory;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.FoodItem;
import com.proj.Model.GameAssetManager; // برای ساخت FoodItem جدید
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CookingManager {
    private List<CookingRecipe> allRecipes;
    private Set<String> learnedRecipeIds; // برای نگهداری دستورالعمل‌های آموخته شده
    private Inventory playerInventory;
    private Inventory refrigeratorInventory; // جدید: اینونتوری یخچال

    private static final int ENERGY_COST_PER_COOK = 3; // هزینه انرژی برای هر بار پخت

    public CookingManager(Inventory playerInventory, Inventory refrigeratorInventory) {
        this.playerInventory = playerInventory;
        this.refrigeratorInventory = refrigeratorInventory;
        this.allRecipes = new ArrayList<>();
        this.learnedRecipeIds = new HashSet<>();
        loadRecipes();
        // برای تست، یک دستور پخت را به صورت پیش‌فرض آموخته شده قرار می‌دهیم
        learnRecipe("Omelet");
        learnRecipe("Pizza");
    }

    private void loadRecipes() {
        // Fried Egg
        Map<String, Integer> friedEggIngredients = new HashMap<>();
        friedEggIngredients.put("Egg", 1);
        allRecipes.add(new CookingRecipe("FriedEgg", new FoodItem("FriedEgg", "Fried Egg", 50, null, 0), friedEggIngredients, false));

        // Baked Fish
        Map<String, Integer> bakedFishIngredients = new HashMap<>();
        bakedFishIngredients.put("Sardine", 1);
        bakedFishIngredients.put("Salmon", 1);
        bakedFishIngredients.put("Wheat", 1);
        allRecipes.add(new CookingRecipe("BakedFish", new FoodItem("BakedFish", "Baked Fish", 75, null, 0), bakedFishIngredients, false));

        // Salad
        Map<String, Integer> saladIngredients = new HashMap<>();
        saladIngredients.put("Leek", 1);
        saladIngredients.put("Dandelion", 1);
        allRecipes.add(new CookingRecipe("Salad", new FoodItem("Salad", "Salad", 113, null, 0), saladIngredients, false));

        // Omelet
        Map<String, Integer> omeletIngredients = new HashMap<>();
        omeletIngredients.put("Egg", 1);
        omeletIngredients.put("Milk", 1);
        allRecipes.add(new CookingRecipe("Omelet", new FoodItem("Omelet", "Omelet", 100, null, 0), omeletIngredients, false));

        // Pumpkin Pie
        Map<String, Integer> pumpkinPieIngredients = new HashMap<>();
        pumpkinPieIngredients.put("Pumpkin", 1);
        pumpkinPieIngredients.put("Wheat Flour", 1);
        pumpkinPieIngredients.put("Milk", 1);
        pumpkinPieIngredients.put("Sugar", 1);
        allRecipes.add(new CookingRecipe("PumpkinPie", new FoodItem("PumpkinPie", "Pumpkin Pie", 225, null, 0), pumpkinPieIngredients, false));

        // Spaghetti
        Map<String, Integer> spaghettiIngredients = new HashMap<>();
        spaghettiIngredients.put("Wheat Flour", 1);
        spaghettiIngredients.put("Tomato", 1);
        allRecipes.add(new CookingRecipe("Spaghetti", new FoodItem("Spaghetti", "Spaghetti", 75, null, 0), spaghettiIngredients, false));

        // Pizza
        Map<String, Integer> pizzaIngredients = new HashMap<>();
        pizzaIngredients.put("Wheat Flour", 1);
        pizzaIngredients.put("Tomato", 1);
        pizzaIngredients.put("Cheese", 1);
        allRecipes.add(new CookingRecipe("Pizza", new FoodItem("Pizza", "Pizza", 150, null, 0), pizzaIngredients, false));

        // Tortilla
        Map<String, Integer> tortillaIngredients = new HashMap<>();
        tortillaIngredients.put("Corn", 1);
        allRecipes.add(new CookingRecipe("Tortilla", new FoodItem("Tortilla", "Tortilla", 50, null, 0), tortillaIngredients, false));

        // Maki Roll
        Map<String, Integer> makiRollIngredients = new HashMap<>();
        makiRollIngredients.put("Any Fish", 1); // "Any Fish" is a placeholder, you might need a more specific ingredient type here
        makiRollIngredients.put("Rice", 1);
        makiRollIngredients.put("Fiber", 1);
        allRecipes.add(new CookingRecipe("MakiRoll", new FoodItem("MakiRoll", "Maki Roll", 100, null, 0), makiRollIngredients, false));

        // Triple Shot Espresso
        Map<String, Integer> espressoIngredients = new HashMap<>();
        espressoIngredients.put("Coffee", 3);
        allRecipes.add(new CookingRecipe("TripleShotEspresso", new FoodItem("TripleShotEspresso", "Triple Shot Espresso", 200, "Max Energy +100", 5), espressoIngredients, false));

        // Cookie
        Map<String, Integer> cookieIngredients = new HashMap<>();
        cookieIngredients.put("Wheat Flour", 1);
        cookieIngredients.put("Sugar", 1);
        cookieIngredients.put("Egg", 1);
        allRecipes.add(new CookingRecipe("Cookie", new FoodItem("Cookie", "Cookie", 90, null, 0), cookieIngredients, false));

        // Hash Browns
        Map<String, Integer> hashBrownsIngredients = new HashMap<>();
        hashBrownsIngredients.put("Potato", 1);
        hashBrownsIngredients.put("Oil", 1);
        allRecipes.add(new CookingRecipe("HashBrowns", new FoodItem("HashBrowns", "Hash Browns", 90, "Farming", 5), hashBrownsIngredients, false));

        // Pancakes
        Map<String, Integer> pancakesIngredients = new HashMap<>();
        pancakesIngredients.put("Wheat Flour", 1);
        pancakesIngredients.put("Egg", 1);
        allRecipes.add(new CookingRecipe("Pancakes", new FoodItem("Pancakes", "Pancakes", 90, "Foraging", 11), pancakesIngredients, false));

        // Fruit Salad
        Map<String, Integer> fruitSaladIngredients = new HashMap<>();
        fruitSaladIngredients.put("Blueberry", 1);
        fruitSaladIngredients.put("Melon", 1);
        fruitSaladIngredients.put("Apricot", 1);
        allRecipes.add(new CookingRecipe("FruitSalad", new FoodItem("FruitSalad", "Fruit Salad", 263, null, 0), fruitSaladIngredients, false));

        // Red Plate
        Map<String, Integer> redPlateIngredients = new HashMap<>();
        redPlateIngredients.put("Red Cabbage", 1);
        redPlateIngredients.put("Radish", 1);
        allRecipes.add(new CookingRecipe("RedPlate", new FoodItem("RedPlate", "Red Plate", 240, "Max Energy +50", 3), redPlateIngredients, false));

        // Bread
        Map<String, Integer> breadIngredients = new HashMap<>();
        breadIngredients.put("Wheat Flour", 1);
        allRecipes.add(new CookingRecipe("Bread", new FoodItem("Bread", "Bread", 50, null, 0), breadIngredients, false));

        // Salmon Dinner
        Map<String, Integer> salmonDinnerIngredients = new HashMap<>();
        salmonDinnerIngredients.put("Salmon", 1);
        salmonDinnerIngredients.put("Amaranth", 1);
        salmonDinnerIngredients.put("Kale", 1);
        allRecipes.add(new CookingRecipe("SalmonDinner", new FoodItem("SalmonDinner", "Salmon Dinner", 125, null, 0), salmonDinnerIngredients, false));

        // Vegetable Medley
        Map<String, Integer> vegetableMedleyIngredients = new HashMap<>();
        vegetableMedleyIngredients.put("Tomato", 1);
        vegetableMedleyIngredients.put("Beet", 1);
        allRecipes.add(new CookingRecipe("VegetableMedley", new FoodItem("VegetableMedley", "Vegetable Medley", 165, null, 0), vegetableMedleyIngredients, false));

        // Farmer's Lunch
        Map<String, Integer> farmersLunchIngredients = new HashMap<>();
        farmersLunchIngredients.put("Omelet", 1);
        farmersLunchIngredients.put("Parsnip", 1);
        allRecipes.add(new CookingRecipe("FarmersLunch", new FoodItem("FarmersLunch", "Farmer's Lunch", 200, "Farming", 5), farmersLunchIngredients, false));

        // Survival Burger
        Map<String, Integer> survivalBurgerIngredients = new HashMap<>();
        survivalBurgerIngredients.put("Bread", 1);
        survivalBurgerIngredients.put("Carrot", 1);
        survivalBurgerIngredients.put("Eggplant", 1);
        allRecipes.add(new CookingRecipe("SurvivalBurger", new FoodItem("SurvivalBurger", "Survival Burger", 125, "Foraging", 5), survivalBurgerIngredients, false));

        // Dish O' the Sea
        Map<String, Integer> dishOTheSeaIngredients = new HashMap<>();
        dishOTheSeaIngredients.put("Sardine", 2);
        dishOTheSeaIngredients.put("Hash Browns", 1);
        allRecipes.add(new CookingRecipe("DishOTheSea", new FoodItem("DishOTheSea", "Dish O' the Sea", 150, "Fishing", 5), dishOTheSeaIngredients, false));

        // Seaform Pudding
        Map<String, Integer> seaformPuddingIngredients = new HashMap<>();
        seaformPuddingIngredients.put("Flounder", 1);
        seaformPuddingIngredients.put("Midnight Carp", 1);
        allRecipes.add(new CookingRecipe("SeaformPudding", new FoodItem("SeaformPudding", "Seaform Pudding", 175, "Fishing", 10), seaformPuddingIngredients, false));

        // Miner's Treat
        Map<String, Integer> minersTreatIngredients = new HashMap<>();
        minersTreatIngredients.put("Carrot", 2);
        minersTreatIngredients.put("Sugar", 1);
        minersTreatIngredients.put("Milk", 1);
        allRecipes.add(new CookingRecipe("MinersTreat", new FoodItem("MinersTreat", "Miner's Treat", 125, "Mining", 5), minersTreatIngredients, false));
    }

    public boolean cook(String recipeName, Player player) {
        CookingRecipe recipeToCook = null;
        for (CookingRecipe recipe : allRecipes) {
            if (recipe.getRecipeName().equalsIgnoreCase(recipeName)) {
                recipeToCook = recipe;
                break;
            }
        }

        if (recipeToCook == null) {
            Gdx.app.log("CookingManager", "Error: Recipe " + recipeName + " not found.");
            return false;
        }

        if (!recipeToCook.isLearned()) {
            Gdx.app.log("CookingManager", "Error: You have not learned " + recipeName + " recipe yet.");
            return false;
        }

        if (player.getCurrentEnergy() < ENERGY_COST_PER_COOK) {
            Gdx.app.log("CookingManager", "Error: Not enough energy to cook " + recipeName + ". Requires " + ENERGY_COST_PER_COOK + " energy.");
            return false;
        }

        if (canCook(recipeToCook)) {
            // Deduct ingredients from player inventory first, then refrigerator
            for (Map.Entry<String, Integer> entry : recipeToCook.getIngredients().entrySet()) {
                String ingredientId = entry.getKey();
                int requiredQuantity = entry.getValue();
                int removedCount = 0;

                // Try to remove from player inventory
                for (Map.Entry<Integer, InventoryItem> playerItemEntry : playerInventory.getItems().entrySet()) {
                    InventoryItem playerItem = playerItemEntry.getValue();
                    if (playerItem != null && playerItem.getId().equals(ingredientId)) {
                        int canRemove = Math.min(requiredQuantity - removedCount, playerItem.getQuantity());
                        playerItem.decreaseQuantity(canRemove);
                        removedCount += canRemove;
                        if (playerItem.getQuantity() <= 0) {
                            playerInventory.removeItem(playerItemEntry.getKey());
                        }
                        if (removedCount == requiredQuantity) break;
                    }
                }

                // If still needed, remove from refrigerator
                if (removedCount < requiredQuantity) {
                    for (Map.Entry<Integer, InventoryItem> fridgeItemEntry : refrigeratorInventory.getItems().entrySet()) {
                        InventoryItem fridgeItem = fridgeItemEntry.getValue();
                        if (fridgeItem != null && fridgeItem.getId().equals(ingredientId)) {
                            int canRemove = Math.min(requiredQuantity - removedCount, fridgeItem.getQuantity());
                            fridgeItem.decreaseQuantity(canRemove);
                            removedCount += canRemove;
                            if (fridgeItem.getQuantity() <= 0) {
                                refrigeratorInventory.removeItem(fridgeItemEntry.getKey());
                            }
                            if (removedCount == requiredQuantity) break;
                        }
                    }
                }
            }

            // Add cooked item to player inventory
            if (!playerInventory.addItem(recipeToCook.getResultItem())) {
                Gdx.app.log("CookingManager", "Error: Player inventory is full, cannot add " + recipeToCook.getResultItem().getName());
                // Revert ingredient deduction if inventory is full (complex, might need transaction-like system)
                return false;
            }

            player.useEnergy(ENERGY_COST_PER_COOK);
            Gdx.app.log("CookingManager", recipeName + " cooked successfully! Energy cost: " + ENERGY_COST_PER_COOK);
            return true;
        } else {
            Gdx.app.log("CookingManager", "Error: Not enough ingredients for " + recipeName + ".");
            return false;
        }
    }

    private boolean canCook(CookingRecipe recipe) {
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String ingredientId = entry.getKey();
            int requiredQuantity = entry.getValue();
            int availableQuantity = 0;

            // Check player inventory
            for (InventoryItem item : playerInventory.getItems().values()) {
                if (item != null && item.getId().equals(ingredientId)) {
                    availableQuantity += item.getQuantity();
                }
            }

            // Check refrigerator inventory
            for (InventoryItem item : refrigeratorInventory.getItems().values()) {
                if (item != null && item.getId().equals(ingredientId)) {
                    availableQuantity += item.getQuantity();
                }
            }

            if (availableQuantity < requiredQuantity) {
                return false; // Not enough of this ingredient
            }
        }
        return true; // All ingredients available
    }

    public List<CookingRecipe> getAvailableRecipes() {
        return allRecipes;
    }

    public boolean isRecipeLearned(String recipeName) {
        return learnedRecipeIds.contains(recipeName);
    }

    public void learnRecipe(String recipeName) {
        for (CookingRecipe recipe : allRecipes) {
            if (recipe.getRecipeName().equalsIgnoreCase(recipeName)) {
                if (!recipe.isLearned()) {
                    recipe.setLearned(true);
                    learnedRecipeIds.add(recipeName);
                    Gdx.app.log("CookingManager", "Learned recipe: " + recipeName);
                } else {
                    Gdx.app.log("CookingManager", "Recipe " + recipeName + " already learned.");
                }
                return;
            }
        }
        Gdx.app.log("CookingManager", "Recipe " + recipeName + " not found to learn.");
    }
}
