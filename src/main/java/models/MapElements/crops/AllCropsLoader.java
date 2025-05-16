package models.MapElements.crops;

import models.MapElements.crops.Plant.PlantInfo;
import models.MapElements.crops.DataReaders.Readers.*;
import models.MapElements.crops.Tree.Fruit;
import models.MapElements.crops.Tree.TreeInfo;

import java.util.ArrayList;
import java.util.List;

public class AllCropsLoader {
    private static final AllCropsLoader instance = new AllCropsLoader();
    public static List<PlantInfo> allPlants;
    public static List<TreeInfo> allTrees;
    public static List<PlantSeed> allPlantSeeds;
    public static List<TreeSeed> allTreeSeeds;
    public static List<ForagingCrop> allForagingCrops;
    public static List<ForagingMineral> allForagingMinerals;
    public static List<ForagingSeed> allForagingSeeds;
    public static List<Fruit> allFruits;

    public void initializeCrops() {
        allPlantSeeds = new ArrayList<>();
        allForagingCrops = new ArrayList<>();
        allForagingMinerals = new ArrayList<>();
        allForagingSeeds = new ArrayList<>();
        allFruits = new ArrayList<>();
        allTreeSeeds = new ArrayList<>();
        allPlants = new ArrayList<>();
        allTrees = new ArrayList<>();
        allPlants = PlantReader.load();
        allTrees = TreeReader.load();
        allForagingSeeds = ForagingSeedReader.load();
        allForagingMinerals = ForagingMineralReader.load();
        allForagingCrops = ForagingCropReader.load();
    }

    public void addFruit(Fruit fruit) {
        allFruits.add(fruit);
    }

    public void addTreeSeed(TreeSeed treeSeed) {
        allTreeSeeds.add(treeSeed);
    }

    public void addPlantSeed(PlantSeed plantSeed) {
        allPlantSeeds.add(plantSeed);
    }

    public static AllCropsLoader getInstance() {
        return instance;
    }

    //find crops by name
    public TreeSeed findTreeSeedByName(String name) {
        for (TreeSeed treeSeed : allTreeSeeds) {
            if (treeSeed.getName().equals(name)) {
                return treeSeed;
            }
        }
        return null;
    }

    public PlantSeed findPlantSeedByName(String name) {
        for (PlantSeed plantSeed : allPlantSeeds) {
            if (plantSeed.getName().equals(name)) {
                return plantSeed;
            }
        }
        return null;
    }
}

