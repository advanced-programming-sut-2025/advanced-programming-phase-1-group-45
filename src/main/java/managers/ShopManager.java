package managers;

import com.google.gson.reflect.TypeToken;
import models.Enums.Shop;
import models.TradeRequest;
import models.User;
import com.google.gson.*;
import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.nio.file.*;
//import static sun.font.FontManagerNativeLibrary.load;

public class ShopManager {
    private UserManager um;
    private final Path storage = Paths.get("shopTransactions.json");
    private final Gson gson = new Gson();
    List<ShopTransaction> history = new ArrayList<>();
    public ShopManager(UserManager userManager) {
        this.um = new UserManager();
        load();
    }

    private void load() {
        try {
            if (Files.exists(storage)) {
                var type = new TypeToken<List<ShopTransaction>>() {}.getType();
                List<ShopTransaction> t = gson.fromJson(Files.readString(storage), type);
                if (t != null) history.addAll(t);
            }
        } catch(IOException ignored) {}
    }

    private void save() {
        try (Writer w = Files.newBufferedWriter(storage)) {
            gson.toJson(history, w);
        } catch(IOException ignored) {}
    }

    public Map<String, Double> getAllProducts(Shop shop) {
       Map<String, Double> products = getAllProducts(shop);
       products.forEach((item, price) -> System.out.printf("%s : %.2f $\n", item, price));
       return products;
    }

    public Map<String, Double> getAvailableProducts(Shop shop) {
        int hour = LocalTime.now().getHour();
        boolean open;
//        if(Shop.getOpenHour() <= hour && shop.getCloseHour() > hour){
//            open = true;
//        } else open = false;
        //if(!open) return Collections.emptyMap();
        Map<String, Double> Map = new LinkedHashMap<>();
        //Map<String, Integer> productsCount = stock.get(shop);
//        shop.getItems().forEach((item) -> {
//            int r = productsCount.getOrDefault(item, 0);
//            if( r > 0) Map.put(item.getName(), item.getPrice());
//        });
        return Map;
    }

    public String purchase(String username, Shop shop, String item, int count) {
        User user = um.getUser(username);
        Map<String, Double> availableProducts = getAvailableProducts(shop);
        if(!availableProducts.containsKey(item)) {
            return "product not available in this shop!";
        }
        if(count <= 0) return "count must be greater than 0!";
//        int remainder = stock.get(shop).get(item);
//        if(count >= remainder) {
//            return String.format("This shop only has %d %s", remainder, item);
//        }
        double total = availableProducts.get(item) * count;
        if(user.getMoney() < total){
            return "you do not have enough money!";
        }
        user.addMoney(-total);
        user.addItem(item, count);
        //stock.get(shop).put(item, remainder - count);
        history.add(new ShopTransaction(username, shop, item, count, total, LocalTime.now()));
        save();
        return "Shopping successfully!";
    }

    public static class ShopTransaction {
        public String user;
        public Shop shop;
        public String item;
        public LocalTime time;
        public int count;
        public double totalPrice;


        public ShopTransaction(String user, Shop shop, String item, int count, double totalPrice, LocalTime time) {
            this.user = user;
            this.shop = shop;
            this.item = item;
            this.time = time;
            this.count = count;
            this.totalPrice = totalPrice;
        }
    }
}
