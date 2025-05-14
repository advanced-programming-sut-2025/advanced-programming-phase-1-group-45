package models.MapElement.crops.DataReaders.Readers;

import jakarta.json.*;
import models.MapElements.crops.Tree.TreeInfo;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TreeReader {
    //  public static void main(String[] args) {
    public static List<TreeInfo> load() {
        List<TreeInfo> trees = new ArrayList<>();

        try (JsonReader reader = Json.
                createReader(new FileReader("src/main/java/models/crops/DataReaders/Data/Trees.json"))) {
            JsonObject root = reader.readObject();
            JsonArray treesArray = root.getJsonArray("Trees");

            for (JsonObject treeObj : treesArray.getValuesAs(JsonObject.class)) {
                // استخراج خصوصیات درخت
                String name = treeObj.getString("name");
                String source = treeObj.getString("source");

                // استخراج مراحل رشد
                JsonArray stagesArray = treeObj.getJsonArray("stages");
                int[] stages = new int[stagesArray.size()];
                for (int i = 0; i < stagesArray.size(); i++) {
                    stages[i] = stagesArray.getInt(i);
                }

                // استخراج خصوصیات میوه
                String fruitName = treeObj.getString("fruit");
                int fruitHarvestCycle = treeObj.getInt("fruitHarvestCycle");
                int fruitBasePrice = treeObj.getInt("fruitBaseSellPrice");
                boolean fruitIsEdible = treeObj.getBoolean("isFruitEdible");

                int fruitEnergy = 0;//cropObj.getInt("energy");
                JsonValue fruitEnergy1 = treeObj.get("fruitEnergy");
                if (fruitEnergy1.getValueType() == JsonValue.ValueType.NUMBER) {
                    JsonNumber fruitEnergy2 = (JsonNumber) fruitEnergy1;
                    fruitEnergy = fruitEnergy2.intValue();
                }

                // استخراج فصلها
                JsonArray seasonArray = treeObj.getJsonArray("season");
                String[] seasons = new String[seasonArray.size()];
                for (int i = 0; i < seasonArray.size(); i++) {
                    seasons[i] = seasonArray.getString(i);
                }

                // ایجاد شیء TreeInfo
                TreeInfo tree = new TreeInfo(
                        name,
                        source,
                        stages,
                        treeObj.getInt("totalHarvestTime"), // totalHarvestDay
                        fruitName,
                        fruitHarvestCycle,
                        fruitBasePrice,
                        fruitIsEdible,
                        fruitEnergy,
                        seasons
                );
                trees.add(tree);
            }

            // نمایش درختان بارگذاری شده
//            for (TreeInfo tree : trees) {
//                System.out.println("درخت بارگذاری شد: " + tree.getName());
//                System.out.println("  میوه: " + tree.getFruit().getName());
//                System.out.println("  فصلها: " + Arrays.toString(tree.getSeason()));
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return trees;
    }
}