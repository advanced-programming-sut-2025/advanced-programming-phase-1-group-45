package models.crops;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sun.source.tree.Tree;
import models.crops.Crop.CropInfo;
import models.crops.Tree.Fruit;
import models.crops.Tree.TreeInfo;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AllCropsLoader {
    private static final AllCropsLoader instance = new AllCropsLoader();
    public static List<CropInfo> allCrops;
    public static List<TreeInfo> allTrees;
    public static List<Seed> allSeeds;
    public static List<ForagingCrop> allForagingCrops;
    public static List<ForagingMineral> allForagingMinerals;
    public static List<ForagingSeed> allForagingSeeds;
    public static List<Fruit> allFruits;
    private static final Gson gson = new Gson();
    String baseAddress = "src/main/java/models/crops/";

    public void initialize() {
        allSeeds = new ArrayList<>();
        allForagingCrops = new ArrayList<>();
        allForagingMinerals = new ArrayList<>();
        allForagingSeeds = new ArrayList<>();
        allCrops = new ArrayList<>();
        allTrees = new ArrayList<>();
        allFruits = new ArrayList<>();
        loadForagingCrops();
        loadForagingMinerals();
        loadForagingSeeds();
        loadCrops();
        loadTrees();
    }

    public void loadForagingCrops() {
        String filePath = baseAddress + "foragingCrops.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<ForagingCrop>>() {
            }.getType();
            allForagingCrops = gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading foragingCrops.json: " + e.getMessage());
        }
    }

    public void loadForagingMinerals() {
        String filePath = baseAddress + "foragingMinerals.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<ForagingMineral>>() {
            }.getType();
            allForagingMinerals = gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading foragingMinerals.json: " + e.getMessage());
        }
    }

    public void loadForagingSeeds() {
        String filePath = baseAddress + "foragingSeeds.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<ForagingSeed>>() {
            }.getType();
            allForagingSeeds = gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading foragingSeeds.json: " + e.getMessage());
        }
    }

    public void loadTrees() {
        String filePath = baseAddress + "Tree/Trees.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<TreeInfo>>() {
            }.getType();
            allTrees = gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading trees.json: " + e.getMessage());
        }
    }

    public void loadCrops() {
        String filePath = baseAddress + "Crop/Crops.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<CropInfo>>() {
            }.getType();
            allCrops = gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading Crops.json: " + e.getMessage());
        }
    }
    public void loadSeeds() {
        String filePath = baseAddress + "Seeds.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<Seed>>() {
            }.getType();
            allSeeds = gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading Seeds.json: " + e.getMessage());
        }
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
