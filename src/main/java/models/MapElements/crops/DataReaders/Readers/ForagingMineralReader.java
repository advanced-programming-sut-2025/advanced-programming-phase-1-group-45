package models.MapElement.crops.DataReaders.Readers;

import jakarta.json.*;
import models.MapElements.crops.ForagingMineral;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ForagingMineralReader {
    //   public static void main(String[] args){
    public static List<ForagingMineral> load() {
        List<ForagingMineral> foragingMinerals = new ArrayList<>();

        try (JsonReader reader = Json.
                createReader(new FileReader("src/main/java/models/crops/DataReaders/Data/foragingMinerals.json"))) {
            JsonObject root = reader.readObject();
            JsonArray mineralArray = root.getJsonArray("foragingMinerals");

            for (JsonObject mineralObj : mineralArray.getValuesAs(JsonObject.class)) {
                String name = mineralObj.getString("name");

                String description = mineralObj.getString("description");

                int sellPrice = 0;
                JsonValue price1 = mineralObj.get("sellPrice");
                if (price1.getValueType() == JsonValue.ValueType.NUMBER) {
                    JsonNumber price2 = (JsonNumber) price1;
                    sellPrice = price2.intValue();
                }
                ForagingMineral foragingMineral = new ForagingMineral(name, description, sellPrice);
                foragingMinerals.add(foragingMineral);
            }

//            for (ForagingMineral tree : foragingMinerals) {
//                System.out.println("درخت بارگذاری شد: " + tree.getName());
//                System.out.println("  فصلها: " + (tree.getSellPrice()));
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return foragingMinerals;
    }
}
