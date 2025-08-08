// CookingRecipe.java
package com.proj.Model.Cooking;

import com.proj.Model.inventoryItems.FoodItem;
import java.util.HashMap;
import java.util.Map;

public class CookingRecipe {
    private final String recipeName;
    private final FoodItem resultItem;
    private final Map<String, Integer> ingredients;

    public CookingRecipe(String recipeName, FoodItem resultItem, Map<String, Integer> ingredients) {
        this.recipeName = recipeName;
        this.resultItem = resultItem;
        this.ingredients = ingredients;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public FoodItem getResultItem() {
        return resultItem;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }
}
