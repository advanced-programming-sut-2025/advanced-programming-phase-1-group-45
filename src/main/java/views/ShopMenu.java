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
            String purchasePart = command.substring("purchase ".length()).trim();

            String productName;
            int count;

            if (purchasePart.contains(" -n ")) {
                // حالت استفاده از -n
                String[] parts = purchasePart.split(" -n ", 2);
                productName = parts[0].trim();
                count = Integer.parseInt(parts[1].trim());
            } else {
                // حالت بدون -n (تعداد در انتهای دستور)
                String[] tokens = purchasePart.split("\\s+");

                if (tokens.length < 1) {
                    System.out.println("invalid command format");
                    return;
                }

                try {
                    count = Integer.parseInt(tokens[tokens.length - 1]);
                    productName = purchasePart.substring(0, purchasePart.lastIndexOf(tokens[tokens.length - 1])).trim();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("add count of goods");
                    return;
                }
            }

            if (count <= 0) {
                System.out.println("count must be greater than zero");
                return;
            }

            String result = sm.purchase(
                    controller.getCurrentUser(),
                    shop,
                    productName,
                    count
            );
            System.out.println(result);

        } catch (NumberFormatException e) {
            System.out.println("invalid count format");
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("invalid command format");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
