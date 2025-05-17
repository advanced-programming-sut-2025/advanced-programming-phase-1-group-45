package models.Cooking;

import models.User;
import java.util.List;
import java.util.Map;

public class CookingCommandHandler {

    public void handleShowCookingRecipes(User user) {
        if (!user.isAtHome()) {
            System.out.println("You need to be at home to access cooking recipes.");
            return;
        }

        List<String> recipes = user.getLearnedCookingRecipes();
        List<String> cookable = user.getCookableRecipes(true);

        System.out.println("=== Cooking Recipes ===");
        for (String recipe : recipes) {
            boolean canCook = cookable.contains(recipe);
            System.out.println((canCook ? "[✓] " : "[✗] ") + recipe);
        }
    }

    public void handleShowCookingRecipeDetails(String[] args, User user) {
        if (args.length < 3) {
            System.out.println("Usage: cooking show recipe <recipe_name>");
            return;
        }

        String recipeName = args[3];
        System.out.println(user.getCookingRecipeDetails(recipeName));
    }

    public void handleCookRecipe(String[] args, User user) {
        if (args.length < 3) {
            System.out.println("Usage: cooking prepare <recipe_name> [--no-refrigerator]");
            return;
        }

        if (!user.isAtHome()) {
            System.out.println("You need to be at home to cook.");
            return;
        }

        String recipeName = args[2];
        boolean useRefrigerator = args.length < 4 || !args[3].equals("--no-refrigerator");

        if (user.cookRecipe(recipeName, useRefrigerator)) {
            System.out.println("Successfully prepared " + recipeName);
        } else {
            System.out.println("Failed to prepare " + recipeName);
        }
    }

    public void handleEatFood(String[] args, User user) {
        if (args.length < 2) {
            System.out.println("Usage: eat <food_name>");
            return;
        }

        String foodName = args[1];

        if (user.eatFood(foodName)) {
            System.out.println("You ate " + foodName + " and gained energy.");
        } else {
            System.out.println("Failed to eat " + foodName);
        }
    }

    public void handleShowRefrigerator(User user) {
        if (!user.isAtHome()) {
            System.out.println("You need to be at home to access the refrigerator.");
            return;
        }

        Map<String, Integer> contents = user.getRefrigeratorContents();

        if (contents.isEmpty()) {
            System.out.println("Your refrigerator is empty.");
        } else {
            System.out.println("=== Refrigerator Contents ===");
            for (Map.Entry<String, Integer> entry : contents.entrySet()) {
                System.out.println(entry.getValue() + "x " + entry.getKey());
            }
        }
    }

    public void handlePutInRefrigerator(String[] args, User user) {
        if (args.length < 4) {
            System.out.println("Usage: cooking refrigerator put <item> <quantity>");
            return;
        }

        if (!user.isAtHome()) {
            System.out.println("You need to be at home to access the refrigerator.");
            return;
        }

        String item = args[3];
        int quantity = Integer.parseInt(args[4]);

        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        if (user.getInventoryCount(item) < quantity) {
            System.out.println("You don't have enough " + item);
            return;
        }

        user.addToRefrigerator(item, quantity);
        System.out.println("Added " + quantity + "x " + item + " to refrigerator");
    }

    public void handlePickFromRefrigerator(String[] args, User user) {
        if (args.length < 4) {
            System.out.println("Usage: cooking refrigerator pick <item> <quantity>");
            return;
        }

        if (!user.isAtHome()) {
            System.out.println("You need to be at home to access the refrigerator.");
            return;
        }

        String item = args[3];
        int quantity = Integer.parseInt(args[4]);

        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        if (user.removeFromRefrigerator(item, quantity)) {
            System.out.println("Removed " + quantity + "x " + item + " from refrigerator");
        } else {
            System.out.println("Not enough " + item + " in refrigerator");
        }
    }

    public void handleCommand(String[] args, User user) {
        if (args.length < 2) {
            System.out.println("Usage: cooking <subcommand>");
            return;
        }

        String subCommand = args[1];

        switch (subCommand) {
            case "recipes":
                handleShowCookingRecipes(user);
                break;

            case "show":
                if (args.length < 3 || !args[2].equals("recipe")) {
                    System.out.println("Usage: cooking show recipe <recipe_name>");
                    return;
                }
                handleShowCookingRecipeDetails(args, user);
                break;

            case "prepare":
                handleCookRecipe(args, user);
                break;

            case "refrigerator":
                if (args.length < 3) {
                    handleShowRefrigerator(user);
                    return;
                }

                switch (args[2]) {
                    case "show":
                        handleShowRefrigerator(user);
                        break;

                    case "put":
                        handlePutInRefrigerator(args, user);
                        break;

                    case "pick":
                        handlePickFromRefrigerator(args, user);
                        break;

                    default:
                        System.out.println("Unknown refrigerator command: " + args[2]);
                }
                break;

            default:
                System.out.println("Unknown cooking command: " + subCommand);
        }
    }
}
