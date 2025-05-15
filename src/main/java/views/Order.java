package views;

import models.Animal.ProductInfo;
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

    public void handleBuildAnimalHouse(String[] args) {
        // build -a <building_name> -t <type> -l <level> -x <x> -y <y>
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

        if (currentUser.createAnimalBuilding(buildingName, type, level, x, y)) {
            currentUser.addMoney(-cost);
            System.out.println("Successfully built " + type + " named " + buildingName);
        } else {
            System.out.println("Failed to build " + type);
        }
    }

    // ارتقای ساختمان دامداری
    public void handleUpgradeAnimalHouse(String[] args) {
        // upgrade -a <building_name>
        if (args.length < 2) {
            System.out.println("Usage: upgrade -a <building_name>");
            return;
        }

        String buildingName = args[2];

        // بررسی وجود ساختمان
        String buildingInfo = currentUser.getBuildingInfo(buildingName);
        if (buildingInfo.equals("Building not found")) {
            System.out.println("Building not found: " + buildingName);
            return;
        }

        // استخراج نوع و سطح فعلی ساختمان
        String type = ""; // "Coop" یا "Barn"
        String currentLevel = ""; // "Regular" یا "Big"

        // این بخش باید با توجه به ساختار واقعی buildingInfo تنظیم شود
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

        if (currentUser.upgradeAnimalBuilding(buildingName)) {
            currentUser.addMoney(-cost);
            System.out.println("Successfully upgraded " + buildingName);
        } else {
            System.out.println("Failed to upgrade " + buildingName);
        }
    }

    // خرید حیوان
    public void handleBuyAnimal(String[] args) {
        // buy animal -a <animal_type> -n <name> -b <building_name>
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

        if (currentUser.addAnimal(animalName, animalType, buildingName)) {
            currentUser.addMoney(-cost);
            System.out.println("Successfully bought " + animalType + " named " + animalName);
        } else {
            System.out.println("Failed to buy animal. Check building capacity and type compatibility.");
        }
    }

    // نوازش حیوان
    public void handlePetAnimal(String[] args) {
        // pet -n <name>
        if (args.length < 2) {
            System.out.println("Usage: pet -n <name>");
            return;
        }

        String animalName = args[2];

        if (currentUser.petAnimal(animalName)) {
            System.out.println("You pet " + animalName + ".");
        } else {
            System.out.println("Animal not found: " + animalName);
        }
    }

    // جمع‌آوری محصول حیوان
    public void handleCollectProduce(String[] args) {
        // collect produce -n <name> -t <tool_name>
        if (args.length < 4) {
            System.out.println("Usage: collect produce -n <name> -t <tool_name>");
            return;
        }

        String animalName = args[3];
        String toolName = args[5];

        // بررسی وجود ابزار در inventory
        if (!toolName.equals("") && Player.getInventoryCount(toolName) <= 0) {
            System.out.println("You don't have " + toolName);
            return;
        }

        ProductInfo product = Player.collectAnimalProduct(animalName, toolName);

        if (product != null) {
            System.out.println("Collected " + product.toString() + " from " + animalName);
        } else {
            System.out.println("Failed to collect product from " + animalName);
        }
    }

    // تغذیه حیوان
    public void handleFeedAnimal(String[] args) {
        // feed hay -n <name>
        if (args.length < 3) {
            System.out.println("Usage: feed hay -n <name>");
            return;
        }

        String animalName = args[3];

        if (Player.feedAnimal(animalName)) {
            System.out.println("Fed " + animalName + " with hay.");
        } else {
            System.out.println("Failed to feed " + animalName + ". Make sure you have hay in your inventory.");
        }
    }

    public void handleShepherdAnimal(String[] args) {
        // shepherd animals -n <name> -l <x> <y> [--inside]
        if (args.length < 6) {
            System.out.println("Usage: shepherd animals -n <name> -l <x> <y> [--inside]");
            return;
        }

        String animalName = args[3];
        int x = Integer.parseInt(args[5]);
        int y = Integer.parseInt(args[6]);
        boolean toInside = args.length >= 8 && args[7].equals("--inside");

        if (Player.shepherdAnimal(animalName, x, y, !toInside)) {
            if (toInside) {
                System.out.println(animalName + " went inside.");
            } else {
                System.out.println(animalName + " went outside to (" + x + ", " + y + ").");
            }
        } else {
            System.out.println("Failed to shepherd " + animalName);
        }
    }

    // فروش حیوان
    public void handleSellAnimal(String[] args) {
        // sell animal -n <name>
        if (args.length < 3) {
            System.out.println("Usage: sell animal -n <name>");
            return;
        }

        String animalName = args[3];

        int sellPrice = Player.sellAnimal(animalName);

        if (sellPrice > 0) {
            Player.addMoney(sellPrice);
            System.out.println("Sold " + animalName + " for " + sellPrice + " gold.");
        } else {
            System.out.println("Animal not found: " + animalName);
        }
    }

    // نمایش لیست حیوانات
    public void handleListAnimals(String[] args) {
        // animals
        List<String> animalsList = currentUser.getAnimalsList();

        if (animalsList.isEmpty()) {
            System.out.println("You don't have any animals.");
        } else {
            System.out.println("=== Your Animals ===");
            for (String animal : animalsList) {
                System.out.println(animal);
            }
        }
    }

    // نمایش لیست حیوانات با محصول جمع‌آوری نشده
    public void handleListProduces(String[] args) {
        // produces
        List<String> animalsWithProduce = currentUser.getAnimalsWithProduce();

        if (animalsWithProduce.isEmpty()) {
            System.out.println("No animals have products to collect.");
        } else {
            System.out.println("=== Animals With Products to Collect ===");
            for (String animal : animalsWithProduce) {
                System.out.println(animal);
            }
        }
    }

    // نمایش اطلاعات حیوان
    public void handleShowAnimal(String[] args) {
        // animal info -n <name>
        if (args.length < 3) {
            System.out.println("Usage: animal info -n <name>");
            return;
        }

        String animalName = args[3];

        String animalInfo = Player.getAnimalInfo(animalName);
        System.out.println(animalInfo);
    }

    // نمایش لیست ساختمان‌های دامداری
    public void handleListBuildings(String[] args) {
        // buildings
        List<String> buildingsList = Player.getBuildingsList();

        if (buildingsList.isEmpty()) {
            System.out.println("You don't have any animal buildings.");
        } else {
            System.out.println("=== Your Animal Buildings ===");
            for (String building : buildingsList) {
                System.out.println(building);
            }
        }
    }

    // نمایش اطلاعات ساختمان دامداری
    public void handleShowBuilding(String[] args) {
        // building info -n <name>
        if (args.length < 3) {
            System.out.println("Usage: building info -n <name>");
            return;
        }

        String buildingName = args[3];

        String buildingInfo = currentUser.getBuildingInfo(buildingName);
        System.out.println(buildingInfo);
    }

    // تنظیم سطح دوستی حیوان (چیت کد)
    public void handleSetFriendship(String[] args) {
        // cheat set friendship -n <animal name> -c <amount>
        if (args.length < 6) {
            System.out.println("Usage: cheat set friendship -n <animal name> -c <amount>");
            return;
        }

        String animalName = args[4];
        int amount = Integer.parseInt(args[6]);

        if (currentUser.setAnimalFriendship(animalName, amount)) {
            System.out.println("Set " + animalName + "'s friendship to " + amount);
        } else {
            System.out.println("Animal not found: " + animalName);
        }
    }

    // محاسبه هزینه ساخت ساختمان
    private int getBuildingCost(String type, String level) {
        if (type.equals("Coop")) {
            switch (level) {
                case "Regular": return 4000;
                case "Big": return 10000;
                case "Deluxe": return 20000;
                default: return 4000;
            }
        } else { // Barn
            switch (level) {
                case "Regular": return 6000;
                case "Big": return 12000;
                case "Deluxe": return 25000;
                default: return 6000;
            }
        }
    }

    // محاسبه هزینه ارتقای ساختمان
    private int getUpgradeCost(String type, String currentLevel) {
        if (type.equals("Coop")) {
            switch (currentLevel) {
                case "Regular": return 10000; // ارتقا به Big
                case "Big": return 20000; // ارتقا به Deluxe
                default: return 0;
            }
        } else { // Barn
            switch (currentLevel) {
                case "Regular": return 12000; // ارتقا به Big
                case "Big": return 25000; // ارتقا به Deluxe
                default: return 0;
            }
        }
    }

    // محاسبه قیمت خرید حیوان
    private int getAnimalCost(String animalType) {
        switch (animalType) {
            case "Chicken": return 800;
            case "Duck": return 1200;
            case "Rabbit": return 4000;
            case "Cow": return 1500;
            case "Goat": return 4000;
            case "Sheep": return 8000;
            case "Pig": return 16000;
            default: return 1000;
        }
    }
}
