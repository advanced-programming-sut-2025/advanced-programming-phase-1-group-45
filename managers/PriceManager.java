package managers;

import java.util.Map;
import java.util.HashMap;

public class PriceManager {
    private static Map<String, Double> baseSellPrice = new HashMap<>();
    static {
        baseSellPrice.put("Cooper Ore", 75.0);

    }

    public static double getBasePrice(String product) {return baseSellPrice.get(product);}

}
