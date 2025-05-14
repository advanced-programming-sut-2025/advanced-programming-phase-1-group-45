package models.crops;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import models.crops.Crop.CropInfo;
import models.crops.Tree.Fruit;
import models.crops.Tree.TreeInfo;

import java.io.*;
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

    public AllCropsLoader() {
        allSeeds = new ArrayList<>();
        allForagingCrops = new ArrayList<>();
        allForagingMinerals = new ArrayList<>();
        allForagingSeeds = new ArrayList<>();
        allCrops = new ArrayList<>();
        allTrees = new ArrayList<>();
        allFruits = new ArrayList<>();
//        loadSeeds();
//        loadForagingCrops();
//        loadForagingMinerals();
//        loadForagingSeeds();
        loadCrops();
//        loadTrees();
    }

    public void loadForagingCrops() {
        String filePath = baseAddress + "foragingCrops.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<ForagingCrop>>() {
            }.getType();
            allForagingCrops = AllCropsLoader.gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading foragingCrops.json: " + e.getMessage());
        }
    }

    public void loadForagingMinerals() {
        String filePath = baseAddress + "foragingMinerals.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<ForagingMineral>>() {
            }.getType();
            allForagingMinerals = AllCropsLoader.gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading foragingMinerals.json: " + e.getMessage());
        }
    }

    public void loadForagingSeeds() {
        String filePath = baseAddress + "foragingSeeds.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<ForagingSeed>>() {
            }.getType();
            allForagingSeeds = AllCropsLoader.gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading foragingSeeds.json: " + e.getMessage());
        }
    }

    public void loadTrees() {
        String filePath = baseAddress + "Tree/Trees.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<TreeInfo>>() {
            }.getType();
            allTrees = AllCropsLoader.gson.fromJson(fr, forageCropsList);
        } catch (Exception e) {
            System.out.println("Error loading trees.json: " + e.getMessage());
        }
    }
    /*
            final Path storage = Paths.get("Crops.json");
    try {
            if (Files.exists(storage)) {
                // اگر JSON ساختار ریشه دارد:
                var wrapperType = new TypeToken<CropWrapper>() {}.getType();
                CropWrapper wrapper = AllCropsLoader.instance.gson.fromJson(Files.readString(storage), wrapperType);
                allCrops = wrapper.getCrops();

                // اگر JSON مستقیم آرایه است:
                // var type = new TypeToken<List<CropInfo>>() {}.getType();
                // allCrops = gson.fromJson(Files.readString(storage), type);
            }
        } catch (IOException e) {
            System.err.println("خطا در خواندن فایل Crops.json:");
            e.printStackTrace();
        }
     */

    public void loadCrops() {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Crops.json");
            InputStreamReader reader = new InputStreamReader(inputStream)){
            Type copyList = new TypeToken<List<CropInfo>>() {}.getType();
            allCrops = gson.fromJson(reader, copyList);
        } catch (Exception e) {
            System.out.println("Error loading Crops.json: " + e.getMessage());
            e.printStackTrace();
            allCrops = new ArrayList<>();
        }
    }
    public void loadSeeds() {
        String filePath = baseAddress + "foragingSeeds.json";
        try (FileReader fr = new FileReader(filePath)) {
            Type forageCropsList = new TypeToken<ArrayList<Seed>>() {
            }.getType();
            allSeeds = AllCropsLoader.gson.fromJson(fr, forageCropsList);
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

//    public static AllCropsLoader getInstance() {
//        return instance;
//    }

    public void addFruit(Fruit fruit) {
        allFruits.add(fruit);
    }
}
