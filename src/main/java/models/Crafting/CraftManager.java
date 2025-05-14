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


        RecipeOfCrafting deluxeScarecrow = new RecipeOfCrafting("Deluxe Scarecrow", "EcoTourism", 2, 2);
        deluxeScarecrow.addIngredient("Wood", 50);
        deluxeScarecrow.addIngredient("Coal", 1);
        deluxeScarecrow.addIngredient("Fiber", 20);
        deluxeScarecrow.addIngredient("Iridium Ore", 1);
        allRecipes.put("Deluxe Scarecrow", deluxeScarecrow);

        RecipeOfCrafting qualitySprinkler = new RecipeOfCrafting("Quality Sprinkler", "Farming", 2, 2);
        qualitySprinkler.addIngredient("Iron Bar", 1);
        qualitySprinkler.addIngredient("Gold Bar", 1);
        allRecipes.put("Quality Sprinkler", qualitySprinkler);

        RecipeOfCrafting iridiumSprinkler = new RecipeOfCrafting("Iridium Sprinkler", "Farming", 3, 2);
        iridiumSprinkler.addIngredient("Gold Bar", 1);
        iridiumSprinkler.addIngredient("Iridium Bar", 1);
        allRecipes.put("Iridium Sprinkler", iridiumSprinkler);


        RecipeOfCrafting cherryBomb = new RecipeOfCrafting("Cherry Bomb", "Extraction", 1, 2);
        cherryBomb.addIngredient("Copper Ore", 4);
        cherryBomb.addIngredient("Coal", 1);
        allRecipes.put("Cherry Bomb", cherryBomb);

        RecipeOfCrafting bomb = new RecipeOfCrafting("Bomb", "Extraction", 2, 2);
        bomb.addIngredient("Iron Ore", 4);
        bomb.addIngredient("Coal", 1);
        allRecipes.put("Bomb", bomb);

        RecipeOfCrafting megaBomb = new RecipeOfCrafting("Mega Bomb",  "Extraction", 3, 2);
        megaBomb.addIngredient("Gold Ore", 4);
        megaBomb.addIngredient("Coal", 1);
        allRecipes.put("Mega Bomb", megaBomb);

        RecipeOfCrafting charcoalKiln = new RecipeOfCrafting("Charcoal Kiln", "EcoTourism", 1, 2);
        charcoalKiln.addIngredient("Wood", 20);
        charcoalKiln.addIngredient("Copper Bar", 2);
        allRecipes.put("Charcoal Kiln", charcoalKiln);

        RecipeOfCrafting furnace = new RecipeOfCrafting("Furnace",  "", 0, 2);
        furnace.addIngredient("Copper Ore", 20);
        furnace.addIngredient("Stone", 25);
        allRecipes.put("Furnace", furnace);


        RecipeOfCrafting beeHouse = new RecipeOfCrafting("Bee House",  "Farming", 1, 2);
        beeHouse.addIngredient("Wood", 40);
        beeHouse.addIngredient("Coal", 8);
        beeHouse.addIngredient("Iron Bar", 1);
        allRecipes.put("Bee House", beeHouse);

        RecipeOfCrafting cheesePress = new RecipeOfCrafting("Cheese Press", "Farming", 2, 2);
        cheesePress.addIngredient("Wood", 45);
        cheesePress.addIngredient("Stone", 45);
        cheesePress.addIngredient("Copper Bar", 1);
        allRecipes.put("Cheese Press", cheesePress);

        RecipeOfCrafting keg = new RecipeOfCrafting("Keg",  "Farming", 3, 2);
        keg.addIngredient("Wood", 30);
        keg.addIngredient("Copper Bar", 1);
        keg.addIngredient("Iron Bar", 1);
        allRecipes.put("Keg", keg);

        RecipeOfCrafting loom = new RecipeOfCrafting("Loom", "Farming", 3, 2);
        loom.addIngredient("Wood", 60);
        loom.addIngredient("Fiber", 30);
        allRecipes.put("Loom", loom);

        RecipeOfCrafting mayonnaiseMachine = new RecipeOfCrafting("Mayonnaise Machine",  "", 0, 2);
        mayonnaiseMachine.addIngredient("Wood", 15);
        mayonnaiseMachine.addIngredient("Stone", 15);
        mayonnaiseMachine.addIngredient("Copper Bar", 1);
        allRecipes.put("Mayonnaise Machine", mayonnaiseMachine);

        RecipeOfCrafting oilMaker = new RecipeOfCrafting("Oil Maker",  "Farming", 3, 2);
        oilMaker.addIngredient("Wood", 100);
        oilMaker.addIngredient("Gold Bar", 1);
        oilMaker.addIngredient("Iron Bar", 1);
        allRecipes.put("Oil Maker", oilMaker);

        RecipeOfCrafting preservesJar = new RecipeOfCrafting("Preserves Jar",  "Farming", 2, 2);
        preservesJar.addIngredient("Wood", 50);
        preservesJar.addIngredient("Stone", 40);
        preservesJar.addIngredient("Coal", 8);
        allRecipes.put("Preserves Jar", preservesJar);

        RecipeOfCrafting fishSmoker = new RecipeOfCrafting("Fish Smoker",  "", 0, 2);
        fishSmoker.addIngredient("Wood", 50);
        fishSmoker.addIngredient("Iron Bar", 3);
        fishSmoker.addIngredient("Coal", 10);
        allRecipes.put("Fish Smoker", fishSmoker);

        RecipeOfCrafting dehydrator = new RecipeOfCrafting("Dehydrator", "", 0, 2);
        dehydrator.addIngredient("Wood", 30);
        dehydrator.addIngredient("Stone", 20);
        dehydrator.addIngredient("Fiber", 30);
        allRecipes.put("Dehydrator", dehydrator);

        RecipeOfCrafting grassStarter = new RecipeOfCrafting("Grass Starter", "", 0, 2);
        grassStarter.addIngredient("Wood", 1);
        grassStarter.addIngredient("Fiber", 1);
        allRecipes.put("Grass Starter", grassStarter);

        RecipeOfCrafting mysticTreeSeed = new RecipeOfCrafting("Mystic Tree Seed", "EcoTourism", 4, 2);
        mysticTreeSeed.addIngredient("Acorn", 5);
        mysticTreeSeed.addIngredient("Maple Seed", 5);
        mysticTreeSeed.addIngredient("Pine Cone", 5);
        mysticTreeSeed.addIngredient("Mahogany Seed", 5);
        allRecipes.put("Mystic Tree Seed", mysticTreeSeed);


        learnedRecipes.add("Scarecrow");
        learnedRecipes.add("Furnace");
        learnedRecipes.add("Mayonnaise Machine");


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
