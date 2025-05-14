package models.Crafting;

import java.util.HashMap;
import java.util.Map;

public class RecipeOfCrafting {

    public String name;
    public Map<String, Integer> ingredients;
    public String skillType;
    public int requiredSkillLevel;
    public int energyCost;

    public RecipeOfCrafting(String name, String skillType, int requiredSkillLevel, int energyCost) {
        this.name = name;
        this.ingredients = new HashMap<>();
        this.skillType = skillType;
        this.requiredSkillLevel = requiredSkillLevel;
        this.energyCost = energyCost;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }

    public String getSkillType() {
        return skillType;
    }

    public int getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public void addIngredient(String item, int quantity) {
        ingredients.put(item, quantity);
    }

    public boolean canCraft(Map<String, Integer> inventory) {
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String item = entry.getKey();
            int requiredQuantity = entry.getValue();


            int availableQuantity = inventory.getOrDefault(item, 0);
            if (availableQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }
}
