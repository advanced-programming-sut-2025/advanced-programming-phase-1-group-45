package models.MapElements.crops.DataReaders.Readers;

import jakarta.json.*;
import models.MapElements.crops.ForagingCrop;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ForagingCropReader {
    //  public static void main(String[] args){
   public static List<ForagingCrop> load() {
        List<ForagingCrop> crops = new ArrayList<>();

        try (JsonReader reader = Json.
                createReader(new FileReader("src/main/java/models/crops/DataReaders/Data/foragingCrops.json"))) {
            JsonObject root = reader.readObject();
            JsonArray cropArray = root.getJsonArray("foragedItems");

            for (JsonObject obj : cropArray.getValuesAs(JsonObject.class)) {
                String name = obj.getString("name");

                JsonArray seasonArray = obj.getJsonArray("season");
                String[] seasons = new String[seasonArray.size()];
                for (int i = 0; i < seasonArray.size(); i++) {
                    seasons[i] = seasonArray.getString(i);
                }
                int sellPrice = 0;
                JsonValue price1 = obj.get("baseSellPrice");
                if (price1.getValueType() == JsonValue.ValueType.NUMBER) {
                    JsonNumber price2 = (JsonNumber) price1;
                    sellPrice = price2.intValue();
                }
                int energy = 0;
                JsonValue energy1 = obj.get("energy");
                if (energy1.getValueType() == JsonValue.ValueType.NUMBER) {
                    JsonNumber energy2 = (JsonNumber) energy1;
                    energy = energy2.intValue();
                }
                ForagingCrop crop = new ForagingCrop(name, seasons, sellPrice, energy);
                crops.add(crop);
            }
//
//            for (ForagingCrop tree : crops) {
//                System.out.println("درخت بارگذاری شد: " + tree.getName());
//                System.out.println("  فصلها: " + Arrays.toString(tree.getSeasons()));
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return crops;
    }
}
