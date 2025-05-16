package views;

import models.Animal.ProductInfo;
import models.Player;
import models.User;

import java.util.List;

public class Order {
    private User currentUser;

    public Order(User user) {
        this.currentUser = user;
    }

    public void showCraftingRecipes() {
        List<String> recipes = currentUser.getPlayer().getLearnedCraftingRecipes();
        List<String> craftable = currentUser.getPlayer().getCraftableRecipes();

        System.out.println("=== Crafting Recipes ===");
        for (String recipe : recipes) {
            boolean canCraft = craftable.contains(recipe);
            System.out.println((canCraft ? "[✓] " : "[✗] ") + recipe);
        }
    }

    public void showCraftingRecipeDetails(String recipeName) {
        String details = currentUser.getPlayer().getCraftingRecipeDetails(recipeName);
        System.out.println(details);
    }

    public void craftItem(String recipeName) {
        if (currentUser.getPlayer().craftItem(recipeName)) {
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

        if (currentUser.getPlayer().useArtisanMachine(machineName, itemName)) {
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
        String product = currentUser.getPlayer().getArtisanProduct(machineName);

        if (product != null) {
            System.out.println("Harvested " + product + " from " + machineName);
        } else {
            System.out.println("Nothing to harvest from " + machineName);
        }
    }

    public void handleArtisanStatus(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: artisan status <machine_name>");
            return;
        }

        String machineName = args[1];
        System.out.println(currentUser.getPlayer().getArtisanMachineStatus(machineName));
    }

    public void handleArtisanList() {
        List<String> machines = currentUser.getPlayer().listArtisanMachines();

        if (machines.isEmpty()) {
            System.out.println("You don't have any artisan machines.");
        } else {
            System.out.println("=== Your Artisan Machines ===");
            for (String machine : machines) {
                System.out.println(machine);
            }
        }
    }

    public void handleBuildAnimalHouse(String[] args) {
        if (args.length < 9) {
            System.out.println("Usage: build -a <building_name> -t <type> -l <level> -x <x> -y <y>");
            return;
        }

        String buildingName = args[2];
        String type = args[4]; // "Coop" یا "Barn"
        String level = args[6]; // "Regular", "Big", "Deluxe"
        int x = Integer.parseInt(args[8]);
        int y = Integer.parseInt(args[10]);

        // بررسی پول کافی
        int cost = getBuildingCost(type, level);
        if (currentUser.getMoney() < cost) {
            System.out.println("Not enough money! You need " + cost + " gold.");
            return;
        }

        String result = currentUser.getPlayer().createAnimalBuilding(buildingName, type, level, x, y);
        if (result.equals("Build successfully")) {
            currentUser.addMoney(-cost);
            System.out.println("Successfully built " + type + " named " + buildingName);
        } else {
            System.out.println(result);
        }
    }

    public void handleUpgradeAnimalHouse(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: upgrade -a <building_name>");
            return;
        }

        String buildingName = args[2];

        // بررسی وجود ساختمان
        String buildingInfo = currentUser.getPlayer().getBuildingInfo(buildingName);
        if (buildingInfo.equals("Building not found")) {
            System.out.println("Building not found: " + buildingName);
            return;
        }

        // استخراج نوع و سطح فعلی ساختمان
        String type = "";
        String currentLevel = "";

        if (buildingInfo.contains("Type: Coop")) {
            type = "Coop";
        } else {
            type = "Barn";
        }

        if (buildingInfo.contains("Level: Regular")) {
            currentLevel = "Regular";
        } else if (buildingInfo.contains("Level: Big")) {
            currentLevel = "Big";
        } else {
            System.out.println("Building is already at maximum level!");
            return;
        }

        // محاسبه هزینه ارتقا
        int cost = getUpgradeCost(type, currentLevel);

        // بررسی پول کافی
        if (currentUser.getMoney() < cost) {
            System.out.println("Not enough money! You need " + cost + " gold.");
            return;
        }

        if (currentUser.getPlayer().upgradeAnimalBuilding(buildingName)) {
            currentUser.addMoney(-cost);
            System.out.println("Successfully upgraded " + buildingName);
        } else {
            System.out.println("Failed to upgrade " + buildingName);
        }
    }

    public void handleBuyAnimal(String[] args) {
        if (args.length < 6) {
            System.out.println("Usage: buy animal -a <animal_type> -n <name> -b <building_name>");
            return;
        }

        String animalType = args[3];
        String animalName = args[5];
        String buildingName = args[7];

        // بررسی قیمت حیوان
        int cost = getAnimalCost(animalType);

        // بررسی پول کافی
        if (currentUser.getMoney() < cost) {
            System.out.println("Not enough money! You need " + cost + " gold.");
            return;
        }

        String result = currentUser.getPlayer().addAnimal(animalName, animalType, buildingName);
        if (result.equals("Add is successfully")) {
            currentUser.addMoney(-cost);
            System.out.println("Successfully bought " + animalType + " named " + animalName);
        } else {
            System.out.println(result);
        }
    }

    public void handlePetAnimal(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: pet -n <name>");
            return;
        }

        String animalName = args[2];

        if (currentUser.getPlayer().petAnimal(animalName)) {
            System.out.println("You pet " + animalName + ".");
        } else {
            System.out.println("Animal not found: " + animalName);
        }
    }

    public void handleCollectProduce(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: collect produce -n <name> -t <tool_name>");
            return;
        }

        String animalName = args[3];
        String toolName = args[5];

        // بررسی وجود ابزار در inventory
        if (!toolName.equals("") && currentUser.getInventoryCount(toolName) <= 0) {
            System.out.println("You don't have " + toolName);
            return;
        }

        ProductInfo product = currentUser.getPlayer().collectAnimalProduct(animalName, toolName);

        if (product != null) {
            System.out.println("Collected " + product.toString() + " from " + animalName);
        } else {
            System.out.println("Failed to collect product from " + animalName);
        }
    }

    public void handleFeedAnimal(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: feed hay -n <name>");
            return;
        }

        String animalName = args[3];

        if (currentUser.getPlayer().feedAnimal(animalName)) {
            System.out.println("Fed " + animalName + " with hay.");
        } else {
            System.out.println("Failed to feed " + animalName + ". Make sure you have hay in your inventory.");
        }
    }

    public void handleShepherdAnimal(String[] args) {
        if (args.length < 6) {
            System.out.println("Usage: shepherd animals -n <name> -l <x> <y> [--inside]");
            return;
        }

        String animalName = args[3];
        int x = Integer.parseInt(args[5]);
        int y = Integer.parseInt(args[6]);
        boolean toInside = args.length >= 8 && args[7].equals("--inside");

        if (currentUser.getPlayer().shepherdAnimal(animalName, x, y, !toInside)) {
            if (toInside) {
                System.out.println(animalName + " went inside.");
            } else {
                System.out.println(animalName + " went outside to (" + x + ", " + y + ").");
            }
        } else {
            System.out.println("Failed to shepherd " + animalName);
        }
    }

    public void handleSellAnimal(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: sell animal -n <name>");
            return;
        }

        String animalName = args[3];

        int sellPrice = currentUser.getPlayer().sellAnimal(animalName);

        if (sellPrice > 0) {
            currentUser.addMoney(sellPrice);
            System.out.println("Sold " + animalName + " for " + sellPrice + " gold.");
        } else {
            System.out.println("Animal not found: " + animalName);
        }
    }

    public void handleListAnimals() {
        List<String> animalsList = currentUser.getPlayer().getAnimalsList();

        if (animalsList.isEmpty()) {
            System.out.println("You don't have any animals.");
        } else {
            System.out.println("=== Your Animals ===");
            for (String animal : animalsList) {
                System.out.println(animal);
            }
        }
    }

    public void handleListProduces() {
        List<String> animalsWithProduce = currentUser.getPlayer().getAnimalsWithProduce();

        if (animalsWithProduce.isEmpty()) {
            System.out.println("No animals have products to collect.");
        } else {
            System.out.println("=== Animals With Products to Collect ===");
            for (String animal : animalsWithProduce) {
                System.out.println(animal);
            }
        }
    }

    public void handleShowAnimal(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: animal info -n <name>");
            return;
        }

        String animalName = args[3];

        String animalInfo = currentUser.getPlayer().getAnimalInfo(animalName);
        System.out.println(animalInfo);
    }

    public void handleListBuildings() {
        List<String> buildingsList = currentUser.getPlayer().getBuildingsList();

        if (buildingsList.isEmpty()) {
            System.out.println("You don't have any animal buildings.");
        } else {
            System.out.println("=== Your Animal Buildings ===");
            for (String building : buildingsList) {
                System.out.println(building);
            }
        }
    }

    public void handleShowBuilding(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: building info -n <name>");
            return;
        }

        String buildingName = args[3];

        String buildingInfo = currentUser.getPlayer().getBuildingInfo(buildingName);
        System.out.println(buildingInfo);
    }

    public void handleSetFriendship(String[] args) {
        if (args.length < 6) {
            System.out.println("Usage: cheat set friendship -n <animal name> -c <amount>");
            return;
        }

        String animalName = args[4];
        int amount = Integer.parseInt(args[6]);

        if (currentUser.getPlayer().setAnimalFriendship(animalName, amount)) {
            System.out.println("Set " + animalName + "'s friendship to " + amount);
        } else {
            System.out.println("Animal not found: " + animalName);
        }
    }

    private int getBuildingCost(String type, String level) {
        if (type.equals("Coop")) {
            switch (level) {
                case "Regular":
                    return 4000;
                case "Big":
                    return 10000;
                case "Deluxe":
                    return 20000;
                default:
                    return 4000;
            }
        } else { // Barn
            switch (level) {
                case "Regular":
                    return 6000;
                case "Big":
                    return 12000;
                case "Deluxe":
                    return 25000;
                default:
                    return 6000;
            }
        }
    }

    private int getUpgradeCost(String type, String currentLevel) {
        if (type.equals("Coop")) {
            switch (currentLevel) {
                case "Regular":
                    return 10000; // ارتقا به Big
                case "Big":
                    return 20000; // ارتقا به Deluxe
                default:
                    return 0;
            }
        } else { // Barn
            switch (currentLevel) {
                case "Regular":
                    return 12000; // ارتقا به Big
                case "Big":
                    return 25000; // ارتقا به Deluxe
                default:
                    return 0;
            }
        }
    }

    private int getAnimalCost(String animalType) {
        switch (animalType) {
            case "Chicken":
                return 800;
            case "Duck":
                return 1200;
            case "Rabbit":
                return 4000;
            case "Cow":
                return 1500;
            case "Goat":
                return 4000;
            case "Sheep":
                return 8000;
            case "Pig":
                return 16000;
            default:
                return 1000;
        }
    }
}
