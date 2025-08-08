// CookingRecipe.java
package com.proj.Model.Cooking;

import com.proj.Model.Cooking.FoodItem;
import java.util.HashMap;
import java.util.Map;

public class CookingRecipe {
    private final String recipeName;
    private final FoodItem resultItem;
    private final Map<String, Integer> ingredients;
    private boolean isLearned;

    public CookingRecipe(String recipeName, FoodItem resultItem, Map<String, Integer> ingredients, boolean isLearned) {
        this.recipeName = recipeName;
        this.resultItem = resultItem;
        this.ingredients = ingredients;
        this.isLearned = isLearned;
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

    public boolean isLearned() {
        return isLearned;
    }

    public void setLearned(boolean learned) {
        isLearned = learned;
    }
}
