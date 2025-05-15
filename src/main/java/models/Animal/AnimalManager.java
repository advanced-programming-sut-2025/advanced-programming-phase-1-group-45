package models.Animal;

import models.Building;
import models.GameMap;
import models.MapElements.Tile.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimalManager {

    public Map<String, Animal> animals;
    public Map<String, String> animalBuildings;
    public Map<String, Building> buildings;

    public AnimalManager() {
        animals = new HashMap<>();
        animalBuildings = new HashMap<>();
        buildings = new HashMap<>();
    }


    public String createBuilding(String buildingName, String type, String level, int x, int y) {
        if (buildings.containsKey(buildingName)) {
            return "This name is already taken ";
        }
        Tile tile = GameMap.getTile(x,y);



        if(tile != null) {
            buildings.put(buildingName, new Building(type, level, x, y));
            return "Build successfully";
        }
        else (tile.hasFeature(isEmpty.class) != null){
            return "No space for building";
        }
    }


    public boolean upgradeBuilding(String buildingName) {
        Building building = buildings.get(buildingName);
        if (building == null) {
            return false;
        }

        return building.upgrade();
    }


    public String addAnimal(String name, String type, String buildingName) {
        if (animals.containsKey(name)) {
            return "This name is already taken";
        }

        Building building = buildings.get(buildingName);
        if (building == null) {
            return "No building";
        }


        if (!building.canHouseAnimalType(type)) {
            return "This Building is not for that animal";
        }


        if (!building.canAddAnimal()) {
            return "Building is full";
        }


        Animal animal = new Animal(name, type);
        animals.put(name, animal);
        animalBuildings.put(name, buildingName);
        building.addAnimal(name);

        return "Add is successfully";
    }


    public boolean petAnimal(String name) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return false;
        }

        animal.pet();
        return true;
    }


    public ProductInfo collectProduct(String name, String toolName) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return null;
        }


        if (!isCorrectToolForAnimal(animal.getType(), toolName)) {
            return null;
        }

        return animal.collectProduct();
    }


    private boolean isCorrectToolForAnimal(String animalType, String toolName) {


        switch (animalType) {
            case "Cow":
            case "Goat":
                return toolName.equals("Milk Pail");
            case "Sheep":
                return toolName.equals("Shears");
            case "Chicken":
            case "Duck":
            case "Rabbit":
                return true;
            case "Pig":

               //return Animal.isWasOutsideToday(); // خوک‌ها فقط وقتی بیرون باشند دنبال قارچ می‌گردند
            default:
                return false;
        }
    }


    public boolean feedHay(String name) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return false;
        }

        animal.feedHay();
        return true;
    }


    public boolean shepherdAnimal(String name, int x, int y, boolean toOutside) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return false;
        }

        if (toOutside) {
            animal.goOutside();
        } else {
            animal.goInside();
        }

        return true;
    }


    public int sellAnimal(String name) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return 0;
        }


        int basePrice = getBasePrice(animal.getType());
        int sellPrice = (int)(basePrice * (0.3 + (animal.getFriendship() / 1000.0)));


        String buildingName = animalBuildings.get(name);
        if (buildingName != null) {
            Building building = buildings.get(buildingName);
            if (building != null) {
                building.removeAnimal(name);
            }
        }


        animals.remove(name);
        animalBuildings.remove(name);

        return sellPrice;
    }


    public boolean setFriendship(String name, int amount) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return false;
        }

        animal.setFriendship(amount);
        return true;
    }


    public void onDayEnd() {
        for (Animal animal : animals.values()) {
            animal.onDayEnd();
        }
    }


    public List<String> getAnimalsList() {
        List<String> animalList = new ArrayList<>();

        for (Map.Entry<String, Animal> entry : animals.entrySet()) {
            Animal animal = entry.getValue();
            animalList.add(animal.getName() + " (" + animal.getType() + ") - Friendship: " + animal.getFriendship());
        }

        return animalList;
    }


    public List<String> getAnimalsWithProduce() {
        List<String> animalList = new ArrayList<>();

        for (Map.Entry<String, Animal> entry : animals.entrySet()) {
            Animal animal = entry.getValue();
            if (!animal.isProduceCollected() && animal.isWasFedToday()) {
                animalList.add(animal.getName() + " (" + animal.getType() + ")");
            }
        }

        return animalList;
    }


    public String getAnimalInfo(String name) {
        Animal animal = animals.get(name);
        if (animal == null) {
            return "Animal not found";
        }

        return animal.getInfo();
    }


    public String getBuildingInfo(String buildingName) {
        Building building = buildings.get(buildingName);
        if (building == null) {
            return "Building not found";
        }

        return building.getInfo();
    }


    public List<String> getBuildingsList() {
        List<String> buildingList = new ArrayList<>();

        for (Map.Entry<String, Building> entry : buildings.entrySet()) {
            String name = entry.getKey();
            Building building = entry.getValue();
            buildingList.add(name + " (" + building.getType() + " - " + building.getLevel() +
                    ") - " + building.getCurrentOccupancy() + "/" + building.getCapacity());
        }

        return buildingList;
    }


    public List<String> getAvailableBuildingsForAnimalType(String animalType) {
        List<String> availableBuildings = new ArrayList<>();

        for (Map.Entry<String, Building> entry : buildings.entrySet()) {
            Building building = entry.getValue();
            if (building.canHouseAnimalType(animalType) && building.canAddAnimal()) {
                availableBuildings.add(entry.getKey());
            }
        }

        return availableBuildings;
    }


    private int getBasePrice(String animalType) {
        switch (animalType) {
            case "Chicken":
                return 800;
            case "Duck":
                return 1200;
            case "Rabbit":
                return 4000;
            case "Cow":
                return 1500;
            case "Goat":
                return 4000;
            case "Sheep":
                return 8000;
            case "Pig":
                return 16000;
            default:
                return 1000;
        }
    }
}
