package models;

import models.Animal.AnimalManager;
import models.Animal.ProductInfo;
import models.Crafting.CraftManager;
import models.Tools.Backpack.Backpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static models.User.addItem;


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
    public static Map<String, ArtisanMachine> artisanMachines = new HashMap<>();
    private static AnimalManager animalManager;

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


    public boolean addArtisanMachine(String machineName, String machineType) {
        if (artisanMachines.containsKey(machineName)) {
            return false;
        }

        artisanMachines.put(machineName, new ArtisanMachine(machineType));
        return true;
    }


    public static boolean useArtisanMachine(String machineName, String inputItem) {
        if (!artisanMachines.containsKey(machineName)) {
            return false;
        }


        if (getInventoryCount(inputItem) <= 0) {
            return false;
        }


        ArtisanMachine machine = artisanMachines.get(machineName);
        if (machine.startProcessing(inputItem)) {
            Backpack.addItemAmount(inputItem, -1);
            return true;
        }

        return false;
    }


    public static String getArtisanProduct(String machineName) {
        if (!artisanMachines.containsKey(machineName)) {
            return null;
        }

        ArtisanMachine machine = artisanMachines.get(machineName);
        String product = machine.getProduct();

        if (product != null) {
            Backpack.addItemAmount(product, 1);
        }

        return product;
    }


    public void updateArtisanMachines(int hours) {
        for (ArtisanMachine machine : artisanMachines.values()) {
            machine.updateTime(hours);
        }
    }


    public static String getArtisanMachineStatus(String machineName) {
        if (!artisanMachines.containsKey(machineName)) {
            return "Machine not found: " + machineName;
        }

        return artisanMachines.get(machineName).getStatus();
    }


    public static List<String> listArtisanMachines() {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, ArtisanMachine> entry : artisanMachines.entrySet()) {
            String machineName = entry.getKey();
            ArtisanMachine machine = entry.getValue();
            result.add(machineName + " - " + machine.getStatus());
        }

        return result;
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

    public static List<String> getBuildingsList() {
        return animalManager.getBuildingsList();
    }

    // متدهای مربوط به حیوانات
    public String addAnimal(String name, String type, String buildingName) {
        return animalManager.addAnimal(name, type, buildingName);
    }

    public boolean petAnimal(String name) {
        return animalManager.petAnimal(name);
    }

    public static ProductInfo collectAnimalProduct(String name, String toolName) {
        ProductInfo product = animalManager.collectProduct(name, toolName);
        if (product != null) {
            // اضافه کردن محصول به inventory
            addItem(product.getQuality() + " " + product.getProductName(), 1);
        }
        return product;
    }

    public static boolean feedAnimal(String name) {
        // بررسی وجود یونجه در inventory
        if (getInventoryCount("Hay") <= 0) {
            return false;
        }

        if (animalManager.feedHay(name)) {
            // کم کردن یونجه از inventory
            addItem("Hay", -1);
            return true;
        }

        return false;
    }

    public static boolean shepherdAnimal(String name, int x, int y, boolean toOutside) {
        return animalManager.shepherdAnimal(name, x, y, toOutside);
    }

    public static int sellAnimal(String name) {
        return animalManager.sellAnimal(name);
    }

    public boolean setAnimalFriendship(String name, int amount) {
        return animalManager.setFriendship(name, amount);
    }

    public void updateAnimals() {
        animalManager.onDayEnd();
    }

    public static List<String> getAnimalsList() {
        return animalManager.getAnimalsList();
    }

    public static List<String> getAnimalsWithProduce() {
        return animalManager.getAnimalsWithProduce();
    }

    public static String getAnimalInfo(String name) {
        return animalManager.getAnimalInfo(name);
    }

    public List<String> getAvailableBuildingsForAnimalType(String animalType) {
        return animalManager.getAvailableBuildingsForAnimalType(animalType);
    }
}
