package models.Crafting;

import models.Energy;
import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftManager {

    public Map<String, RecipeOfCrafting> allRecipes;
    public List<String> learnedRecipes;


    public CraftManager() {
        allRecipes = new HashMap<>();
        learnedRecipes = new ArrayList<>();
        initializeRecipes();
    }


    private void initializeRecipes() {

        RecipeOfCrafting scarecrow = new RecipeOfCrafting("Scarecrow", "Farming", 1, 2);
        scarecrow.addIngredient("Wood", 50);
        scarecrow.addIngredient("Coal", 1);
        scarecrow.addIngredient("Fiber", 20);
        allRecipes.put("Scarecrow", scarecrow);


        RecipeOfCrafting sprinkler = new RecipeOfCrafting("Sprinkler", "Farming", 2, 2);
        sprinkler.addIngredient("Copper Bar", 1);
        sprinkler.addIngredient("Iron Bar", 1);
        allRecipes.put("Sprinkler", sprinkler);


    }

    public boolean learnRecipe(String recipeName) {
        if (allRecipes.containsKey(recipeName) && !learnedRecipes.contains(recipeName)) {
            learnedRecipes.add(recipeName);
            return true;
        }
        return false;
    }


    public List<String> unlockRecipesBySkillLevel(String skillType, int skillLevel) {
        List<String> newlyUnlocked = new ArrayList<>();

        for (Map.Entry<String, RecipeOfCrafting> entry : allRecipes.entrySet()) {
            String recipeName = entry.getKey();
            RecipeOfCrafting recipe = entry.getValue();

            if (recipe.getSkillType().equals(skillType) &&
                    recipe.getRequiredSkillLevel() <= skillLevel &&
                    !learnedRecipes.contains(recipeName)) {
                learnedRecipes.add(recipeName);
                newlyUnlocked.add(recipeName);
            }
        }

        return newlyUnlocked;
    }

    public boolean craftItem(String recipeName, User user) {
        if (!learnedRecipes.contains(recipeName)) {
            return false;
        }

        RecipeOfCrafting recipe = allRecipes.get(recipeName);
        if (recipe == null) {
            return false;
        }


        int skillLevel = getSkillLevel(user, recipe.getSkillType());
        if (skillLevel < recipe.getRequiredSkillLevel()) {
            return false;
        }


        Map<String, Integer> inventory = getUserInventory(user);
        if (!recipe.canCraft(inventory)) {
            return false;
        }


        if (!user.getPlayer().consumeEnergy(recipe.getEnergyCost())) {
            return false;
        }


        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();
            user.addItem(item, -quantity);
        }


        user.addItem(recipe.getName(), 1);


        increaseSkillExperience(user, recipe.getSkillType(), 5);

        return true;
    }


    public List<String> getLearnedRecipes() {
        return new ArrayList<>(learnedRecipes);
    }


    public List<String> getCraftableRecipes(User user) {
        List<String> craftable = new ArrayList<>();
        Map<String, Integer> inventory = getUserInventory(user);

        for (String recipeName : learnedRecipes) {
            RecipeOfCrafting recipe = allRecipes.get(recipeName);
            if (recipe != null && recipe.canCraft(inventory)) {
                craftable.add(recipeName);
            }
        }

        return craftable;
    }


    public String getRecipeDetails(String recipeName) {
        RecipeOfCrafting recipe = allRecipes.get(recipeName);
        if (recipe == null) {
            return "Recipe not found";
        }

        StringBuilder details = new StringBuilder();
        details.append("Recipe: ").append(recipe.getName()).append("\n");
        details.append("Required Skill: ").append(recipe.getSkillType())
                .append(" Level ").append(recipe.getRequiredSkillLevel()).append("\n");
        details.append("Energy Cost: ").append(recipe.getEnergyCost()).append("\n");
        details.append("Ingredients:\n");

        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            details.append("  ").append(entry.getValue())
                    .append("x ").append(entry.getKey()).append("\n");
        }

        return details.toString();
    }


    private int getSkillLevel(User user, String skillType) {
        Energy energy= user.getPlayer().getEnergy();
        switch (skillType) {
            case "Farming":
                return energy.getFarmingLevel();
            case "Extraction":
                return energy.getExtractionLevel();
            case "EcoTourism":
                return energy.getEcoTourismLevel();
            case "Fishing":
                return energy.getFishingLevel();
            default:
                return 0;
        }
    }

    private void increaseSkillExperience(User user, String skillType, int amount) {
        Energy energy = user.getPlayer().getEnergy();
        switch (skillType) {
            case "Farming":
                energy.increaseFarmingXP(amount);
                break;
            case "Extraction":
                energy.increaseExtractionXP(amount);
                break;
            case "EcoTourism":
                energy.increaseEcoTourismXP(amount);
                break;
            case "Fishing":
                energy.increaseFishingXP(amount);
                break;
        }
    }

    private Map<String, Integer> getUserInventory(User user) {
        // این متد باید از User اطلاعات inventory را استخراج کند
        // برای مثال می‌توان از یک Map برای نگهداری آیتم‌ها و تعداد آنها استفاده کرد
        Map<String, Integer> inventory = new HashMap<>();

        // TODO: پیاده‌سازی استخراج inventory از User
        // برای فعلاً به صورت خالی برمی‌گردانیم

        return inventory;
    }
}
