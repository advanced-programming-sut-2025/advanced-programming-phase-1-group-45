package models.Cooking;

import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookingManager {
    private Map<String, Recipe> allRecipes;
    private List<String> learnedRecipes;
    private Map<String, Integer> refrigerator;

    public CookingManager() {
        allRecipes = new HashMap<>();
        learnedRecipes = new ArrayList<>();
        refrigerator = new HashMap<>();
        initializeRecipes();
    }

    private void initializeRecipes() {
        Recipe salad = new Recipe("Salad", 113, "Farming", 1, 11, 220, "Diner");
        salad.addIngredient("Leek", 1);
        salad.addIngredient("Dandelion", 1);
        salad.addIngredient("Vinegar", 1);
        allRecipes.put("Salad", salad);

        Recipe omelette = new Recipe("Omelette", 198, 250, "Diner");
        omelette.addIngredient("Egg", 1);
        omelette.addIngredient("Milk", 1);
        allRecipes.put("Omelette", omelette);

        Recipe pizza = new Recipe("Pizza", 300, "Farming", 2, 5, 600, "Saloon");
        pizza.addIngredient("Wheat Flour", 1);
        pizza.addIngredient("Tomato", 1);
        pizza.addIngredient("Cheese", 1);
        allRecipes.put("Pizza", pizza);

        Recipe mushroomSoup = new Recipe("Mushroom Soup", 200, "Foraging", 2, 7, 340, "Diner");
        mushroomSoup.addIngredient("Common Mushroom", 1);
        mushroomSoup.addIngredient("Morel", 1);
        allRecipes.put("Mushroom Soup", mushroomSoup);

        Recipe strawberryJam = new Recipe("Strawberry Jam", 150, 290, "Pierre");
        strawberryJam.addIngredient("Strawberry", 3);
        strawberryJam.addIngredient("Sugar", 1);
        allRecipes.put("Strawberry Jam", strawberryJam);

        learnedRecipes.add("Salad");
        learnedRecipes.add("Omelette");
    }

    public void addToRefrigerator(String item, int quantity) {
        int current = refrigerator.getOrDefault(item, 0);
        refrigerator.put(item, current + quantity);
    }

    public boolean removeFromRefrigerator(String item, int quantity) {
        int current = refrigerator.getOrDefault(item, 0);
        if (current < quantity) {
            return false;
        }

        refrigerator.put(item, current - quantity);
        if (current - quantity <= 0) {
            refrigerator.remove(item);
        }
        return true;
    }

    public Map<String, Integer> getRefrigeratorContents() {
        return new HashMap<>(refrigerator);
    }

    public boolean cookRecipe(String recipeName, User user, boolean useRefrigerator) {
        if (!learnedRecipes.contains(recipeName)) {
            return false;
        }

        Recipe recipe = allRecipes.get(recipeName);
        if (recipe == null) {
            return false;
        }

        if (!user.getPlayer().consumeEnergy(3)) {
            return false;
        }

        Map<String, Integer> inventory = getUserInventory(user);

        if (!recipe.canCook(inventory, useRefrigerator ? refrigerator : new HashMap<>())) {
            return false;
        }

        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();

            int fromInventory = Math.min(quantity, inventory.getOrDefault(item, 0));
            if (fromInventory > 0) {
                user.addItem(item, -fromInventory);
                quantity -= fromInventory;
            }

            if (quantity > 0 && useRefrigerator) {
                removeFromRefrigerator(item, quantity);
            }
        }

        user.addItem(recipe.getName(), 1);

        return true;
    }

    public boolean learnRecipe(String recipeName) {
        if (allRecipes.containsKey(recipeName) && !learnedRecipes.contains(recipeName)) {
            learnedRecipes.add(recipeName);
            return true;
        }
        return false;
    }

    public List<String> getLearnedRecipes() {
        return new ArrayList<>(learnedRecipes);
    }

    public List<String> getCookableRecipes(User user, boolean useRefrigerator) {
        List<String> cookable = new ArrayList<>();
        Map<String, Integer> inventory = getUserInventory(user);

        for (String recipeName : learnedRecipes) {
            Recipe recipe = allRecipes.get(recipeName);
            if (recipe != null && recipe.canCook(inventory, useRefrigerator ? refrigerator : new HashMap<>())) {
                cookable.add(recipeName);
            }
        }

        return cookable;
    }

    public String getRecipeDetails(String recipeName) {
        Recipe recipe = allRecipes.get(recipeName);
        if (recipe == null) {
            return "Recipe not found";
        }

        StringBuilder details = new StringBuilder();
        details.append("=== ").append(recipe.getName()).append(" ===\n");
        details.append("Energy: +").append(recipe.getEnergyValue()).append("\n");

        if (recipe.hasBuff()) {
            details.append("Buff: ").append(recipe.getBuffType())
                    .append(" +").append(recipe.getBuffValue())
                    .append(" (").append(recipe.getBuffDuration()).append("h)\n");
        }

        if (recipe.getSource() != null && !recipe.getSource().isEmpty()) {
            details.append("Source: ").append(recipe.getSource()).append("\n");
        }

        details.append("Sell Price: ").append(recipe.getBasePrice()).append("g\n\n");
        details.append("Ingredients:\n");

        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            details.append("  ").append(entry.getValue())
                    .append("x ").append(entry.getKey()).append("\n");
        }

        return details.toString();
    }

    public boolean eatFood(String foodName, User user) {
        if (user.getInventoryCount(foodName) <= 0) {
            return false;
        }

        Recipe recipe = allRecipes.get(foodName);
        if (recipe == null) {
            return false;
        }

        user.addItem(foodName, -1);

        user.getPlayer().addEnergy(recipe.getEnergyValue());

        if (recipe.hasBuff()) {
            applyBuff(user, recipe.getBuffType(), recipe.getBuffDuration(), recipe.getBuffValue());
        }

        return true;
    }

    private void applyBuff(User user, String buffType, int duration, int value) {
        System.out.println("Applied " + buffType + " buff +" + value + " for " + duration + " hours");

        switch (buffType) {
            case "Farming":
                user.getPlayer().increaseFarmingXP(value * 10);
                break;
            case "Foraging":
                user.getPlayer().increaseEcoTourismXP(value * 10);
                break;
            case "Fishing":
                user.getPlayer().increaseFishingXP(value * 10);
                break;
            case "Mining":
                user.getPlayer().increaseExtractionXP(value * 10);
                break;
        }
    }

    private Map<String, Integer> getUserInventory(User user) {
        Map<String, Integer> inventory = new HashMap<>();

        // در اینجا باید اطلاعات inventory کاربر را استخراج کنیم
        // با توجه به ساختار کلاس User شما، می‌توانیم به صورت زیر عمل کنیم

        // فرض کنید متدی به نام getAllItems در User وجود داشته باشد
        // return user.getAllItems();

        // یا به روشی دیگر، مثلاً با استفاده از یک لیست از آیتم‌های ممکن
        String[] possibleItems = {"Leek", "Dandelion", "Vinegar", "Egg", "Milk",
                "Wheat Flour", "Tomato", "Cheese", "Common Mushroom",
                "Morel", "Strawberry", "Sugar"};

        for (String item : possibleItems) {
            int count = user.getInventoryCount(item);
            if (count > 0) {
                inventory.put(item, count);
            }
        }

        return inventory;
    }
}
