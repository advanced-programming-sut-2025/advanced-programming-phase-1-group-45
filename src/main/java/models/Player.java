package models;

import models.Crafting.CraftManager;

import java.util.List;

public class Player {
    public Energy energy;
    private boolean isCollapsed;
    private String equippedTool;
    private String backpackType;
    private int backpackCapacity;
    private String trashCanType;
    public CraftManager craftManager;
    public boolean isAtHome;
    public User user;

    public Player(int initialEnergy) {
        this.energy = new Energy(initialEnergy);
        this.isCollapsed = false;
        this.equippedTool = null;
        this.backpackType = "Basic Backpack";
        this.backpackCapacity = 12;
        this.trashCanType = "Basic Trash Can";
        this.craftManager = new CraftManager();
        this.isAtHome = false;
    }

    public boolean craftItem(String recipeName) {
        return craftManager.craftItem(recipeName, user);
    }

    public List<String> getLearnedCraftingRecipes() {
        return craftManager.getLearnedRecipes();
    }

    public List<String> getCraftableRecipes() {
        return craftManager.getCraftableRecipes(user);
    }

    public String getCraftingRecipeDetails(String recipeName) {
        return craftManager.getRecipeDetails(recipeName);
    }


    public String showEnergy() {
        return energy.showEnergy();
    }

    public boolean consumeEnergy(int amount) {
        boolean success = energy.consumeEnergy(amount);
        if (!success && amount > 0) {
            collapse();
        }
        return success;
    }

    public void setEnergy(int value) {
        energy.setEnergy(value);
    }

    public void setUnlimitedEnergy() {
        energy.setUnlimitedEnergy();
    }

    public void collapse() {
        isCollapsed = true;
        energy.collapse();
        System.out.println("You've collapsed due to exhaustion!");
    }

    public void restoreEnergyForNewDay() {
        energy.restoreEnergyForNewDay(isCollapsed);
        isCollapsed = false;
    }

    public void equipTool(String toolName) {
        this.equippedTool = toolName;
    }

    public String getCurrentTool() {
        return equippedTool;
    }

    public void upgradeBackpack(String newType) {
        this.backpackType = newType;
        switch (newType) {
            case "Large Backpack":
                this.backpackCapacity = 24;
                break;
            case "Deluxe Backpack":
                this.backpackCapacity = Integer.MAX_VALUE;
                break;
        }
    }

    public void upgradeTrashCan(String newType) {
        this.trashCanType = newType;
    }

    public double getTrashCanReturnRate() {
        switch (trashCanType) {
            case "Copper Trash Can":
                return 0.15;
            case "Iron Trash Can":
                return 0.30;
            case "Gold Trash Can":
                return 0.45;
            case "Iridium Trash Can":
                return 0.60;
            default:
                return 0.0;
        }
    }

    public int getBackpackCapacity() {
        return backpackCapacity;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public Energy getEnergy() {
        return energy;
    }

    public String getBackpackType() {
        return backpackType;
    }

    public String getTrashCanType() {
        return trashCanType;
    }
}
