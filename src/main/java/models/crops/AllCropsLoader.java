package models.crops;

import managers.TimeManager;
import models.Foraging;
import models.crops.Crop.CropInfo;
import models.crops.Tree.Fruit;
import models.crops.Tree.TreeInfo;

import java.util.ArrayList;
import java.util.List;

public class AllCropsLoader {
    private static final AllCropsLoader instance = new AllCropsLoader();
    public static List<CropInfo> allCrops;
    public static List<TreeInfo> allTrees;
    public static List<Seed> allSeeds;
    public static List<Foraging> allForages;
    public static List<Fruit> allFruits;

    public void initialize() {
        allSeeds = new ArrayList<>();
        allForages = new ArrayList<>();
        allCrops = new ArrayList<>();
        allTrees = new ArrayList<>();
        allFruits = new ArrayList<>();
    }

    public Seed findSeed(String name) {
        for (Seed seed : allSeeds) {
            if (seed.getName().equalsIgnoreCase(name)) {
                return seed;
            }
        }
        return null;
    }
    public static AllCropsLoader getInstance() {
        return instance;
    }

    public void addFruit(Fruit fruit) {
        allFruits.add(fruit);
    }
}
