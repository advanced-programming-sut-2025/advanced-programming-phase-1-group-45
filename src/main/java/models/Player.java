package models;

import models.Animal.AnimalManager;
import models.Animal.ProductInfo;
import models.Crafting.CraftManager;
import models.Tools.Backpack.Backpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, ArtisanMachine> artisanMachines;
    private AnimalManager animalManager;

    public Player(int initialEnergy) {
        this.energy = new Energy(initialEnergy);
        this.isCollapsed = false;
        this.equippedTool = null;
        this.backpackType = "Basic Backpack";
        this.backpackCapacity = 12;
        this.trashCanType = "Basic Trash Can";
        this.craftManager = new CraftManager();
        this.isAtHome = false;
        this.animalManager = new AnimalManager();
        this.artisanMachines = new HashMap<>();
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

    // Artisan Machines
    public boolean addArtisanMachine(String machineName, String machineType) {
        if (artisanMachines.containsKey(machineName)) {
            return false;
        }

        artisanMachines.put(machineName, new ArtisanMachine(machineType));
        return true;
    }

    public boolean useArtisanMachine(String machineName, String inputItem) {
        if (!artisanMachines.containsKey(machineName)) {
            return false;
        }

        if (user.getInventoryCount(inputItem) <= 0) {
            return false;
        }

        ArtisanMachine machine = artisanMachines.get(machineName);
        if (machine.startProcessing(inputItem)) {
            user.addItem(inputItem, -1);
            return true;
        }

        return false;
    }

    public String getArtisanProduct(String machineName) {
        if (!artisanMachines.containsKey(machineName)) {
            return null;
        }

        ArtisanMachine machine = artisanMachines.get(machineName);
        String product = machine.getProduct();

        if (product != null) {
            user.addItem(product, 1);
        }

        return product;
    }

    public void updateArtisanMachines(int hours) {
        for (ArtisanMachine machine : artisanMachines.values()) {
            machine.updateTime(hours);
        }
    }

    public String getArtisanMachineStatus(String machineName) {
        if (!artisanMachines.containsKey(machineName)) {
            return "Machine not found: " + machineName;
        }

        return artisanMachines.get(machineName).getStatus();
    }

    public List<String> listArtisanMachines() {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, ArtisanMachine> entry : artisanMachines.entrySet()) {
            String machineName = entry.getKey();
            ArtisanMachine machine = entry.getValue();
            result.add(machineName + " - " + machine.getStatus());
        }

        return result;
    }

    // Animal Management
    public void setGameMap(GameMap map) {
        animalManager.setGameMap(map);
    }

    public String createAnimalBuilding(String buildingName, String type, String level, int x, int y) {
        return animalManager.createBuilding(buildingName, type, level, x, y);
    }

    public boolean upgradeAnimalBuilding(String buildingName) {
        return animalManager.upgradeBuilding(buildingName);
    }

    public String getBuildingInfo(String buildingName) {
        return animalManager.getBuildingInfo(buildingName);
    }

    public List<String> getBuildingsList() {
        return animalManager.getBuildingsList();
    }

    public String addAnimal(String name, String type, String buildingName) {
        return animalManager.addAnimal(name, type, buildingName);
    }

    public boolean petAnimal(String name) {
        return animalManager.petAnimal(name);
    }

    public ProductInfo collectAnimalProduct(String name, String toolName) {
        ProductInfo product = animalManager.collectProduct(name, toolName);
        if (product != null) {
            user.addItem(product.getQuality() + " " + product.getProductName(), 1);
        }
        return product;
    }

    public boolean feedAnimal(String name) {
        if (user.getInventoryCount("Hay") <= 0) {
            return false;
        }

        if (animalManager.feedHay(name)) {
            user.addItem("Hay", -1);
            return true;
        }

        return false;
    }

    public boolean shepherdAnimal(String name, int x, int y, boolean toOutside) {
        return animalManager.shepherdAnimal(name, x, y, toOutside);
    }

    public int sellAnimal(String name) {
        return animalManager.sellAnimal(name);
    }

    public boolean setAnimalFriendship(String name, int amount) {
        return animalManager.setFriendship(name, amount);
    }

    public void updateAnimals() {
        animalManager.onDayEnd();
    }

    public List<String> getAnimalsList() {
        return animalManager.getAnimalsList();
    }

    public List<String> getAnimalsWithProduce() {
        return animalManager.getAnimalsWithProduce();
    }

    public String getAnimalInfo(String name) {
        return animalManager.getAnimalInfo(name);
    }

    public List<String> getAvailableBuildingsForAnimalType(String animalType) {
        return animalManager.getAvailableBuildingsForAnimalType(animalType);
    }

    // Skill XP methods
    public void increaseFarmingXP(int amount) {
        energy.increaseFarmingXP(amount);
    }

    public void increaseExtractionXP(int amount) {
        energy.increaseExtractionXP(amount);
    }

    public void increaseEcoTourismXP(int amount) {
        energy.increaseEcoTourismXP(amount);
    }

    public void increaseFishingXP(int amount) {
        energy.increaseFishingXP(amount);
    }

    public int getFarmingLevel() {
        return energy.getFarmingLevel();
    }

    public int getExtractionLevel() {
        return energy.getExtractionLevel();
    }

    public int getEcoTourismLevel() {
        return energy.getEcoTourismLevel();
    }

    public int getFishingLevel() {
        return energy.getFishingLevel();
    }

    public void addEnergy(int amount) {
        energy.addEnergy(amount);
    }
}

    public List<String> getAvailableBuildingsForAnimalType(String animalType) {
        return animalManager.getAvailableBuildingsForAnimalType(animalType);
    }
}
