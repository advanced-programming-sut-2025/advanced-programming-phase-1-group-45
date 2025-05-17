package models.Cooking;

import java.util.HashMap;
import java.util.Map;

public class Recipe {
    private String name;
    private Map<String, Integer> ingredients;
    private int energyValue;
    private String buffType;
    private int buffValue;
    private int buffDuration;
    private int basePrice;
    private String source;

    public Recipe(String name, int energyValue, int basePrice, String source) {
        this.name = name;
        this.ingredients = new HashMap<>();
        this.energyValue = energyValue;
        this.buffType = null;
        this.buffValue = 0;
        this.buffDuration = 0;
        this.basePrice = basePrice;
        this.source = source;
    }

    public Recipe(String name, int energyValue, String buffType, int buffValue, int buffDuration, int basePrice, String source) {
        this(name, energyValue, basePrice, source);
        this.buffType = buffType;
        this.buffValue = buffValue;
        this.buffDuration = buffDuration;
    }

    public void addIngredient(String item, int quantity) {
        ingredients.put(item, quantity);
    }

    public boolean canCook(Map<String, Integer> inventory, Map<String, Integer> refrigerator) {
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String item = entry.getKey();
            int requiredQuantity = entry.getValue();

            int inventoryQuantity = inventory.getOrDefault(item, 0);
            int refrigeratorQuantity = refrigerator.getOrDefault(item, 0);

            if (inventoryQuantity + refrigeratorQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getIngredients() {
        return new HashMap<>(ingredients);
    }

    public int getEnergyValue() {
        return energyValue;
    }

    public boolean hasBuff() {
        return buffType != null;
    }

    public String getBuffType() {
        return buffType;
    }

    public int getBuffValue() {
        return buffValue;
    }

    public int getBuffDuration() {
        return buffDuration;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public String getSource() {
        return source;
    }
}
