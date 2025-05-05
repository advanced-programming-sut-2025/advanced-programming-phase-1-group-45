package models;

public class Crafting {
    package models;

import java.util.Map;

    public class Craftsmanship {
        private GameMap<Recipe, Boolean> knownRecipes;
        private Energy energy;

        public void craftItem(Recipe recipe) {
            if (hasMaterials(recipe) && energy.hasEnough(recipe.getEnergyCost())) {
                // Crafting implementation
                energy.consume(recipe.getEnergyCost());
            }
        }

        /* private boolean hasMaterials(Recipe recipe) {
            // Material check logic
        } */

        public void learnNewDesign() {
        }

        public void putOnGround() {
            Backpack backpack = new Backpack();
            MovementController movementController = new MovementController();
        }

        public void addItemToInventory(Tool tool) {
            Backpack backpack = new Backpack();
        }
    }
}
