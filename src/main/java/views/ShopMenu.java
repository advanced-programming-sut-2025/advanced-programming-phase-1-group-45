package views;

import controllers.MenuController;
import managers.ShopManager;
import models.Enums.Shop;

import java.util.Map;

public class ShopMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller) {
        ShopManager sm = controller.getShopManager();
        String shopName = controller.getCurrentUser().getCurrentShop().toUpperCase();
        Shop shop = null;
        if(shopName.equals("BLACKSMITH")) {
            shop = Shop.BLACKSMITH;
        } else if(shopName.equals("JOJA_MART")) {
            shop = Shop.JOJA_MART;
        } else if(shopName.equals(" GENERAL_STORE")) {
            shop = Shop. GENERAL_STORE;
        }else if(shopName.equals("CARPENTER")) {
            shop = Shop.CARPENTER;
        }else if(shopName.equals("FISH_SHOP")) {
            shop = Shop.FISH_SHOP;
        }else if(shopName.equals("SALOON")) {
            shop = Shop.SALOON;
        }else if(shopName.equals("RANCH")) {
            shop = Shop.RANCH;
        }
        if(command.equals("show current menu")){
            System.out.println("You are in shop menu");
        } else if(command.equals("exit shop")){
            System.out.println("exiting shop");
            controller.getCurrentUser().setCurrentShop(null);
            controller.setCurrentMenu(new GameMenu());
        }
        else if(command.equals("show all products")) {
            sm.getAllProducts(shop);
        }
        else if(command.equals("show all available products")) {
            Map<String, Double> products = sm.getAvailableProducts(shop);
            products.forEach((name, price) ->
                    System.out.printf("%s : %.2f $\n", name, price));
        }
        else if (command.startsWith("tools ")) {
            if (!shop.equals(Shop.BLACKSMITH)) {
                System.out.println("You should be in blacksmith's shop to upgrade tools!");
                return;
            } else {
                BlacksmithMenu blacksmithMenu = new BlacksmithMenu();
                blacksmithMenu.handleCommand(command, controller);
            }
        }
        else if(command.startsWith("purchase ")) {
            handlePurchaseCommand(command, sm, controller, shop);
        }
        else System.out.println("Invalid command");
    }
    private void handlePurchaseCommand(String command, ShopManager sm, MenuController controller, Shop shop) {
        try {
            String[] parts = command.split(" -n ");
            if(parts.length != 2) {
                System.out.println("Invalid format! Use: purchase <product_name> -n <count>");
                return;
            }

            String productName = parts[0].replace("purchase ", "").trim();
            int count = Integer.parseInt(parts[1].trim());
            String result = sm.purchase(
                    controller.getCurrentUser().getUsername(),
                    shop, // Replace with actual shop instance
                    productName,
                    count
            ); System.out.println(result);

        } catch(NumberFormatException e) {
            System.out.println("Invalid quantity format!");
        }
    }
}
