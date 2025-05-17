package views;

import controllers.WeatherController;
import managers.GameManager;
import controllers.MenuController;
import managers.ShopManager;
import managers.UserManager;
import models.Animal.ProductInfo;
import models.Enums.Command;
import models.Enums.Shop;
import models.Enums.Weather;
import models.GameSession;
import models.GameMap;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileType;
import models.MapElements.crops.AllCropsLoader;

import java.util.*;
import java.util.regex.Matcher;

public class GameMenu implements Menu {
    @Override
    public void handleCommand(String command, MenuController controller) {
        GameManager gm = controller.getGameManager();
        String me = controller.getCurrentUser().getUsername(); //current player
        ShopManager sm = controller.getShopManager();
        UserManager um = controller.getUserManager();
        GameSession gs = controller.getCurrentSession();
        Matcher matcher;
        if (command.startsWith("game new ")) {
            GameSession s = gm.createNewGame(command, me, controller);
            if (s != null) {
                controller.setCurrentSession(s);
                System.out.println("New Game Created, now choose your map");
            } else System.out.println("New Game Creation Failed, make sure to enter input correctly.");
        } else if (command.startsWith("game map")) {
            int size = Integer.parseInt(command.split(" ")[2]);
            GameMap map = new GameMap(size, true);
            //if 1,2,3 maps
            if (controller.getCurrentSession() != null && gm.selectMap(controller.getCurrentSession(), command)) {
                controller.getCurrentSession().setMap(map);
                System.out.println("map selected");
            }
        } else if (command.equals("load game")) {
            GameSession s = gm.loadLastSession(controller.getCurrentUser());
            if (s != null) {
                controller.setCurrentSession(s);
                System.out.println("the last game loaded");
            } else System.out.println("no game exists");
        } else if (command.equals("next turn")) {
            GameSession s = controller.getCurrentSession();
            if (s != null) {
                s.nextTurn();
                System.out.println("it's " + s.getTurn() + " turn!");
            }
        } else if (command.equals("exit game")) {
            GameSession s = controller.getCurrentSession();
            if (!me.equals(s.getPlayers().get(0))) {
                System.out.println("only the creator can exit the game");
            } else {
                controller.getGameManager().saveSession(s);

                controller.setCurrentSession(null);
                System.out.println("exiting the game");
            }
        } else if (command.equals("force terminate")) {
            GameSession s = controller.getCurrentSession();
            s.startVote(me);
            System.out.println("voting terminated");
        } else if (command.startsWith("vote")) {
            GameSession s = controller.getCurrentSession();
            if (!s.isVoteInProgress()) {
                System.out.println("no active voting");
            } else if (s.hasVoted(me)) {
                System.out.println("you already voted");
            } else {
                boolean yes = command.equals("vote yes");
                s.recordVote(me, yes);
                System.out.println("vote submitted");
                if (s.allVoted()) {
                    if (s.isVoteSuccessful()) {
                        controller.getGameManager().endSession(s);
                        System.out.println("All votes were positive ,ending the game");
                        controller.setCurrentSession(null);
                        controller.setCurrentMenu(new MainMenu());
                    } else {
                        s.clearVote();
                        System.out.println("there is at least one negative vote, continuing the game");
                    }
                }
            }
        } else if (command.equals("help reading map")) {
            GameMap.printMapLegend();
        } else if (command.equals("show current menu")) {
            System.out.println("game menu");
        } else if (command.equals("menu exit")) {
            System.out.println("moving to main menu");
            controller.setCurrentMenu(new MainMenu());
        } else if (command.startsWith("walk -l")) {
            GameMap.handleWalkCommand(command, controller);
        } else if (command.startsWith("print map")) {
            //GameMap.handlePrintMap(command, controller);
            Map<String, String> o = parseOptions(command.split("\\s+"));
            int[] coords = handleCoords(o.get("-l"));
            int x = coords[0];
            int y = coords[1];
            int s = Integer.parseInt(o.get("-s"));
            controller.getCurrentSession().getMap().printMapArea(x, y, s);

        } else if (command.startsWith("sell")) {
            um.handleSell(command, controller);
        } else if (command.equals("show all products")) {
            sm.getAllProducts(Shop.GENERAL_STORE);
        } else if (command.equals("show all available products")) {
            sm.getAvailableProducts(Shop.GENERAL_STORE);
        } else if (command.startsWith("purchase ")) {
            handlePurchaseCommand(command, sm, controller);
        } else if ((matcher = Command.TIME.getMatcher(command)) != null) {
            System.out.println(gs.getTimeManager().getTimeString());
        } else if ((matcher = Command.DATE.getMatcher(command)) != null) {
            System.out.println(gs.getTimeManager().getDay() + " of " + gs.getTimeManager().getSeason().toString());
        } else if ((matcher = Command.dateTime.getMatcher(command)) != null) {
            System.out.println(gs.getTimeManager().getDateAndTimeString());
        } else if ((matcher = Command.DayOfWeek.getMatcher(command)) != null) {
            System.out.println(gs.getTimeManager().getDayOfWeek());
        } else if ((matcher = Command.CheatTime.getMatcher(command)) != null) {
            for (int i = 0; i < Integer.parseInt(matcher.group("hour")); i++) {
                gs.getTimeManager().advanceHour();
            }
        } else if ((matcher = Command.CheatDate.getMatcher(command)) != null) {
            for (int i = 0; i < Integer.parseInt(matcher.group("day")); i++) {
                gs.getTimeManager().advanceDay();
            }
        } else if ((matcher = Command.Season.getMatcher(command)) != null) {
            System.out.println(gs.getTimeManager().getSeason().toString());
        } else if ((matcher = Command.Weather.getMatcher(command)) != null) {
            System.out.println(WeatherController.getInstance().getCurrentWeather());
        } else if ((matcher = Command.WeatherForecast.getMatcher(command)) != null) {
            System.out.println(WeatherController.getInstance().getForecastWeather());
        } else if ((matcher = Command.CheatWeather.getMatcher(command)) != null) {
            WeatherController.getInstance().setCurrentWeather(matcher.group(1));
            System.out.println("Weather changed to " + WeatherController.getInstance().getCurrentWeather());
        } else if (command.startsWith("tools ")) {
            handleToolsCommand(command, gs, controller);
        } else if ((matcher = Command.craftInfo.getMatcher(command)) != null) {
            AllCropsLoader.getInstance().printCraftInfo(matcher.group(1));
        } else if ((matcher = Command.plantSeed.getMatcher(command)) != null) {
            if (gs.getToolManager().findTargetTile(matcher.group("direction"), controller.getCurrentUser().getPlayer()).
                    equals(gs.getMap().getTile(gs.getPlayerX(), gs.getPlayerY()))) {
                System.out.println("You can not use any tool int this tile");
                return;
            }
            controller.getCurrentUser().getPlayer().getFarmingManager().plant(matcher.group(1),
                    gs.getToolManager().findTargetTile(matcher.group("direction"),
                            controller.getCurrentUser().getPlayer()));
        } else if ((matcher = Command.showPlant.getMatcher(command)) != null) {
             Tile tile = gs.getToolManager().findTargetTile(matcher.group("direction"),
                     controller.getCurrentUser().getPlayer());
             if (tile == null) {
                 System.out.println("Tile is out of bounds");
                 return;
             }
            controller.getCurrentUser().getPlayer().getFarmingManager().showPlant(tile);
        }
        else if((matcher = Command.fertilize.getMatcher(command)) != null) {
            Tile tile = gs.getToolManager().findTargetTile(matcher.group(2), controller.getCurrentUser().getPlayer());
            if (tile == null) {
                System.out.println("Tile is out of bounds");
                return;
            }
            controller.getCurrentUser().getPlayer().getFarmingManager().fertilize(matcher.group(1), tile);
        } else if(command.equals("howmuch water")) {
            System.out.println(controller.getCurrentUser().getPlayer().getBackpack().getWateringCan().getWaterAmount());
        }
        else if (command.startsWith("artisan use ")) {
            String[] parts = command.substring(12).split(" ");
            if (parts.length < 2) {
                System.out.println("Usage: artisan use <artisan_name> <item_name>");
                return;
            }

            String artisanName = parts[0];
            String itemName = parts[1];

            if (controller.getCurrentUser().getInventoryCount(itemName) <= 0) {
                System.out.println("You don't have any " + itemName + " in your inventory!");
                return;
            }

            boolean success = controller.getCurrentUser().getPlayer().useArtisanMachine(artisanName, itemName);
            if (success) {
                System.out.println("Started processing " + itemName + " in " + artisanName);
            } else {
                System.out.println("Failed to use " + artisanName + ". Make sure the machine exists and can process this item.");
            }
        }
        else if (command.startsWith("artisan get ")) {
            String artisanName = command.substring(12);

            String product = controller.getCurrentUser().getPlayer().getArtisanProduct(artisanName);
            if (product != null) {
                System.out.println("Collected " + product + " from " + artisanName);
            } else {
                System.out.println("No product ready to collect from " + artisanName + " or machine not found.");
            }
        }

        else if (command.startsWith("pet -n ")) {
            String animalName = command.substring(6);
            boolean success = controller.getCurrentUser().getPlayer().petAnimal(animalName);
            if (success) {
                System.out.println("You pet " + animalName + ".");
            } else {
                System.out.println("Failed to pet " + animalName + ". Animal not found.");
            }
        }
        else if (command.startsWith("feed hay -n ")) {
            String animalName = command.substring(11);

            if (controller.getCurrentUser().getInventoryCount("Hay") <= 0) {
                System.out.println("You don't have any hay in your inventory!");
                return;
            }

            boolean success = controller.getCurrentUser().getPlayer().feedAnimal(animalName);
            if (success) {
                System.out.println("You fed " + animalName + " with hay.");
            } else {
                System.out.println("Failed to feed " + animalName + ". Animal not found or already fed today.");
            }
        }
        else if (command.startsWith("shepherd animals -n ")) {
            String[] parts = command.split(" -l ");
            if (parts.length != 2) {
                System.out.println("Usage: shepherd animals -n <animal name> -l <x,y>");
                return;
            }

            String animalName = parts[0].substring(20);
            String[] coords = parts[1].split(" ");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);

            boolean toOutside = true;
            if (coords.length > 2 && coords[2].equals("--inside")) {
                toOutside = false;
            }

            boolean success = controller.getCurrentUser().getPlayer().shepherdAnimal(animalName, x, y, toOutside);
            if (success) {
                if (toOutside) {
                    System.out.println(animalName + " went outside to (" + x + ", " + y + ").");
                } else {
                    System.out.println(animalName + " went inside.");
                }
            } else {
                System.out.println("Failed to shepherd " + animalName + ".");
            }
        }
        else if (command.equals("animals")) {
            List<String> animals = controller.getCurrentUser().getPlayer().getAnimalsList();
            if (animals.isEmpty()) {
                System.out.println("You don't have any animals.");
            } else {
                System.out.println("=== Your Animals ===");
                for (String animal : animals) {
                    System.out.println(animal);
                }
            }
        }
        else if (command.equals("produces")) {
            List<String> produces = controller.getCurrentUser().getPlayer().getAnimalsWithProduce();
            if (produces.isEmpty()) {
                System.out.println("No animals have products to collect.");
            } else {
                System.out.println("=== Animals With Products to Collect ===");
                for (String animal : produces) {
                    System.out.println(animal);
                }
            }
        }
        else if (command.startsWith("collect produce -n ")) {
            String animalName = command.substring(19);

            String toolName = "";

            String animalInfo = controller.getCurrentUser().getPlayer().getAnimalInfo(animalName);
            if (animalInfo.contains("Cow") || animalInfo.contains("Goat")) {
                toolName = "Milk Pail";
            } else if (animalInfo.contains("Sheep")) {
                toolName = "Shears";
            }

            if (!toolName.isEmpty() && controller.getCurrentUser().getInventoryCount(toolName) <= 0) {
                System.out.println("You need a " + toolName + " to collect from this animal!");
                return;
            }

            ProductInfo product = controller.getCurrentUser().getPlayer().collectAnimalProduct(animalName, toolName);
            if (product != null) {
                System.out.println("Collected " + product.toString() + " from " + animalName);
            } else {
                System.out.println("Failed to collect product from " + animalName);
            }
        }

        else if (command.startsWith("crafting show recipes")) {
            List<String> recipes = controller.getCurrentUser().getPlayer().getLearnedCraftingRecipes();
            System.out.println("=== Available Crafting Recipes ===");
            for (String recipe : recipes) {
                System.out.println("- " + recipe);
            }
        }
        else if (command.startsWith("crafting craft ")) {
            String recipeName = command.substring(15);
            boolean success = controller.getCurrentUser().getPlayer().craftItem(recipeName);
            if (success) {
                System.out.println("Successfully crafted " + recipeName);
            } else {
                System.out.println("Failed to craft " + recipeName + ". Make sure you have the required materials and energy.");
            }
        }

        else if (command.startsWith("cooking refrigerator ")) {
            String[] parts = command.substring(20).split(" ");
            if (parts.length < 2) {
                System.out.println("Usage: cooking refrigerator [put/pick] <item>");
                return;
            }

            String action = parts[0];
            String itemName = parts[1];

            if (!controller.getCurrentUser().getPlayer().isAtHome) {
                System.out.println("You must be at home to use the refrigerator!");
                return;
            }

            if (action.equals("put")) {
                if (controller.getCurrentUser().getInventoryCount(itemName) <= 0) {
                    System.out.println("You don't have any " + itemName + " in your inventory!");
                    return;
                }

                controller.getCurrentUser().getPlayer().addToRefrigerator(itemName, 1);
                System.out.println("Added " + itemName + " to refrigerator");
            } else if (action.equals("pick")) {
                boolean success = controller.getCurrentUser().getPlayer().removeFromRefrigerator(itemName, 1);
                if (success) {
                    System.out.println("Picked " + itemName + " from refrigerator");
                } else {
                    System.out.println("Failed to pick " + itemName + " from refrigerator");
                }
            } else {
                System.out.println("Invalid action! Use 'put' or 'pick'");
            }
        }
        else if (command.equals("cooking show recipes")) {
            List<String> recipes = controller.getCurrentUser().getPlayer().getLearnedCookingRecipes();
            System.out.println("=== Available Cooking Recipes ===");
            for (String recipe : recipes) {
                System.out.println("- " + recipe);
            }
        }
        else if (command.startsWith("cooking prepare ")) {
            String recipeName = command.substring(16);

            if (!controller.getCurrentUser().getPlayer().isAtHome) {
                System.out.println("You must be at home to cook!");
                return;
            }

            boolean success = controller.getCurrentUser().getPlayer().cookRecipe(recipeName, true); 
            if (success) {
                System.out.println("Successfully cooked " + recipeName);
            } else {
                System.out.println("Failed to cook " + recipeName + ". Make sure you have the required ingredients and energy.");
            }
        }
        else if (command.startsWith("eat ")) {
            String foodName = command.substring(4);

            if (controller.getCurrentUser().getInventoryCount(foodName) <= 0) {
                System.out.println("You don't have any " + foodName + " in your inventory!");
                return;
            }

            boolean success = controller.getCurrentUser().getPlayer().eatFood(foodName);
            if (success) {
                System.out.println("You ate " + foodName);
            } else {
                System.out.println("Failed to eat " + foodName);
            }
        }


        else if (command.startsWith("fishing -p ")) {
            String fishingPole = command.substring(11);

            if (controller.getCurrentUser().getInventoryCount(fishingPole) <= 0) {
                System.out.println("You don't have a " + fishingPole + " in your inventory!");
                return;
            }

            // بررسی نزدیکی به آب
            boolean nearWater = false;
            int playerX = gs.getPlayerX();
            int playerY = gs.getPlayerY();

            // بررسی کاشی‌های اطراف برای وجود آب
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Tile tile = gs.getMap().getTile(playerX + dx, playerY + dy);
                    if (tile != null && tile.getTileType() == TileType.LAKE) {
                        nearWater = true;
                        break;
                    }
                }
                if (nearWater) break;
            }

            if (!nearWater) {
                System.out.println("You need to be near water to fish!");
                return;
            }

            // ایجاد نمونه‌های مورد نیاز برای ماهیگیری
            models.fish.FishManager fishManager = new models.fish.FishManager();
            models.fish.Fishing fishing = new models.fish.Fishing(fishManager);

            // تعیین فصل و آب و هوا
            String season = gs.getTimeManager().getSeason().toString();
            String weather = WeatherController.getInstance().getCurrentWeather().toString();

            // ماهیگیری
            List<models.fish.FishCatch> catches = fishing.goFishing(fishingPole, season, weather, controller.getCurrentUser().getPlayer().getEnergy());

            if (catches.isEmpty()) {
                System.out.println("You didn't catch any fish!");
            } else {
                System.out.println("You caught " + catches.size() + " fish:");
                for (models.fish.FishCatch fishCatch : catches) {
                    System.out.println("- " + fishCatch.toString());
                    // اضافه کردن ماهی به انبار
                    controller.getCurrentUser().addItem(fishCatch.getQuality() + " " + fishCatch.getFishName(), 1);
                }
            }
        }


        else System.out.println("invalid command");
    }

    private void handlePurchaseCommand(String command, ShopManager sm, MenuController controller) {
        try {
            String[] parts = command.split(" -n ");
            if (parts.length != 2) {
                System.out.println("Invalid format! Use: purchase <product_name> -n <count>");
                return;
            }

            String productName = parts[0].replace("purchase ", "").trim();
            int count = Integer.parseInt(parts[1].trim());

            String result = sm.purchase(
                    controller.getCurrentUser(),
                    Shop.GENERAL_STORE, // Replace with actual shop instance
                    productName,
                    count
            );

            System.out.println(result);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity format!");
        }
    }

    private int[] handleCoords(String coords) {
        String[] x = coords.split(",");
        return new int[]{Integer.parseInt(x[0]), Integer.parseInt(x[1])};
    }

    private Map<String, String> parseOptions(String[] options) {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < options.length - 1; i++) {
            if (options[i].startsWith("-")) {
                map.put(options[i], options[i + 1]);
            }
        }
        return map;
    }

    private void handleToolsCommand(String command, GameSession gs, MenuController controller) {
        Matcher matcher;
        if ((matcher = Command.ToolEquip.getMatcher(command)) != null) {
            gs.getToolManager().toolEquip(matcher.group("toolName"), controller.getCurrentUser().getPlayer());
        } else if ((matcher = Command.toolShowCurrent.getMatcher(command)) != null) {
            System.out.println(gs.getToolManager().toolShowCurrent(controller.getCurrentUser().getPlayer()));
        } else if ((matcher = Command.toolsShow.getMatcher(command)) != null) {
            gs.getToolManager().showAllToolsAvailable(controller.getCurrentUser().getPlayer());
        } else if ((matcher = Command.toolsUpgrade.getMatcher(command)) != null) {
            gs.shopMenu().handleCommand(command);
        } else if ((matcher = Command.toolUse.getMatcher(command)) != null) {
            gs.getToolManager().useTool(matcher.group("direction"), controller.getCurrentUser().getPlayer());
        }
    }
}

