package views;

import models.Player;
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


    public void handleArtisanUse(String[] args) {
        
        if (args.length < 3) {
            System.out.println("Usage: artisan use <machine_name> <item_name>");
            return;
        }

        String machineName = args[1];
        String itemName = args[2];

        if (Player.useArtisanMachine(machineName, itemName)) {
            System.out.println("Started processing " + itemName + " in " + machineName);
        } else {
            System.out.println("Failed to use " + machineName);
        }
    }

    
    public void handleArtisanGet(String[] args) {
      
        if (args.length < 2) {
            System.out.println("Usage: artisan get <machine_name>");
            return;
        }

        String machineName = args[1];
        String product = Player.getArtisanProduct(machineName);

        if (product != null) {
            System.out.println("Harvested " + product + " from " + machineName);
        } else {
            System.out.println("Nothing to harvest from " + machineName);
        }
    }

   
    public void handleArtisanStatus(String[] args) {
        // artisan status <machine_name>
        if (args.length < 2) {
            System.out.println("Usage: artisan status <machine_name>");
            return;
        }

        String machineName = args[1];
        System.out.println(Player.getArtisanMachineStatus(machineName));
    }

    
    public void handleArtisanList(String[] args) {
        
        List<String> machines =Player.listArtisanMachines();

        if (machines.isEmpty()) {
            System.out.println("You don't have any artisan machines.");
        } else {
            System.out.println("=== Your Artisan Machines ===");
            for (String machine : machines) {
                System.out.println(machine);
            }
        }
    }
}
