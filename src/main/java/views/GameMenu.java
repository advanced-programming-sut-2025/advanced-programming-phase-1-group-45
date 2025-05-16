package views;

import controllers.WeatherController;
import managers.GameManager;
import controllers.MenuController;
import managers.ShopManager;
import managers.UserManager;
import models.Enums.Command;
import models.Enums.Shop;
import models.GameSession;
import models.GameMap;

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
        } else System.out.println("invalid command");
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
        } else if((matcher = Command.toolShowCurrent.getMatcher(command)) != null) {
            System.out.println(gs.getToolManager().toolShowCurrent(controller.getCurrentUser().getPlayer()));
        } else if((matcher = Command.toolsShow.getMatcher(command)) != null) {
            gs.getToolManager().showAllToolsAvailable(controller.getCurrentUser().getPlayer());
        } else if ((matcher = Command.toolsUpgrade.getMatcher(command)) != null) {
            gs.shopMenu().handleCommand(command);
        } else if ((matcher = Command.toolUse.getMatcher(command)) != null) {
            gs.getToolManager().useTool(matcher.group("direction"), controller.getCurrentUser().getPlayer());
        }
    }
}
