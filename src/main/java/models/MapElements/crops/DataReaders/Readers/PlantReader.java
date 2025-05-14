package models.MapElements.crops.DataReaders.Readers;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.*;
import models.MapElements.crops.Plant.PlantInfo;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PlantReader {
    //  public static void main(String[] args) {
    public static List<PlantInfo> load() {
        List<PlantInfo> crops = new ArrayList<>();
        try (
                JsonReader reader = Json.
                        createReader(new FileReader("src/main/java/models/crops/DataReaders/Data/PlantsPlants.json"))) {
            JsonObject root = reader.readObject();
            JsonArray cropsArray = root.getJsonArray("Plants");

            for (JsonObject cropObj : cropsArray.getValuesAs(JsonObject.class)) {
                String name = cropObj.getString("name");
                String source = cropObj.getString("source");

                JsonArray stagesArray = cropObj.getJsonArray("stages");
                int[] stages = new int[stagesArray.size()];
                for (int i = 0; i < stagesArray.size(); i++) {
                    stages[i] = stagesArray.getInt(i);
                }

                int totalHarvestTime = cropObj.getInt("totalHarvestTime");
                boolean isOneTime = cropObj.getBoolean("isOneTime");

                Integer regrowthTime = null;
                if (cropObj.containsKey("regrowthTime") && !cropObj.isNull("regrowthTime")) {
                    regrowthTime = cropObj.getInt("regrowthTime");
                }

                int baseSellPrice = 0;

                JsonValue baseSellPrice1 = cropObj.get("baseSellPrice");
                if (baseSellPrice1.getValueType() == JsonValue.ValueType.NUMBER) {
                    JsonNumber baseSellPrice2 = (JsonNumber) baseSellPrice1;
                    baseSellPrice = baseSellPrice2.intValue();
                }

                boolean isEdible = cropObj.getBoolean("isEdible");

                int energy = 0;//cropObj.getInt("energy");
                JsonValue energy1 = cropObj.get("energy");
                if (energy1.getValueType() == JsonValue.ValueType.NUMBER) {
                    JsonNumber energy2 = (JsonNumber) energy1;
                    energy = energy2.intValue();
                }

                // Parse seasons array
                JsonArray seasonArray = cropObj.getJsonArray("season");
                String[] seasons = new String[seasonArray.size()];
                for (int i = 0; i < seasonArray.size(); i++) {
                    seasons[i] = seasonArray.getString(i);
                }

                boolean canBecomeGiant = cropObj.getBoolean("canBecomeGiant");

                // Create CropInfo instance
                PlantInfo crop = new PlantInfo(
                        name, source, stages, totalHarvestTime, isOneTime, regrowthTime,
                        baseSellPrice, isEdible, energy, seasons, canBecomeGiant
                );
                crops.add(crop);
            }

            //  Print crop names
//            for (CropInfo crop : crops) {
//                System.out.println("Loaded crop: " + crop.getName());
//                System.out.println(Arrays.toString(crop.getSeason()));
//            }
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
        return crops;
    }
}

//}


