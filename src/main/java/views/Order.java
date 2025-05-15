package views;

import models.User;

import java.util.List;

public class Order {

    public void showCraftingRecipes(User user) {
        List<String> recipes = user.getPlayer().getLearnedCraftingRecipes();
        List<String> craftable = user.getPlayer().getCraftableRecipes();

        System.out.println("=== Crafting Recipes ===");
        for (String recipe : recipes) {
            boolean canCraft = craftable.contains(recipe);
            System.out.println((canCraft ? "[✓] " : "[✗] ") + recipe);
        }
    }


    public void showCraftingRecipeDetails(User user, String recipeName) {
        String details = user.getPlayer().getCraftingRecipeDetails(recipeName);
        System.out.println(details);
    }


    public void craftItem(User user, String recipeName) {
        if (user.getPlayer().craftItem(recipeName)) {
            System.out.println("Successfully crafted " + recipeName);
        } else {
            System.out.println("Failed to craft " + recipeName);
        }
    }
}
