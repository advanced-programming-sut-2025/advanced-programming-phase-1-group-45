package models.MapElements.crops.DataReaders.Readers;

import jakarta.json.*;
import models.MapElements.crops.ForagingSeed;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ForagingSeedReader {
  //  public static void main(String[] args){
    public static List<ForagingSeed> load() {
        List<ForagingSeed> foragingSeeds = new ArrayList<>();

        try (JsonReader reader = Json.
                createReader(new FileReader("src/main/java/models/crops/DataReaders/Data/foragingSeeds.json"))) {
            JsonObject root = reader.readObject();
            JsonArray seedsArray = root.getJsonArray("seeds");

            for (JsonObject seedObj : seedsArray.getValuesAs(JsonObject.class)) {
                String name = seedObj.getString("name");

                JsonArray seasonArray = seedObj.getJsonArray("season");
                String[] seasons = new String[seasonArray.size()];
                for (int i = 0; i < seasonArray.size(); i++) {
                    seasons[i] = seasonArray.getString(i);
                }
                ForagingSeed foragingSeed = new ForagingSeed(name, seasons);
                foragingSeeds.add(foragingSeed);
            }
//
//            for (ForagingSeed tree : foragingSeeds) {
//                System.out.println("درخت بارگذاری شد: " + tree.getName());
//                System.out.println("  فصلها: " + Arrays.toString(tree.getSeasons()));
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return foragingSeeds;
    }
}
