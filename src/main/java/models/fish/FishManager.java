package models.fish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishManager {

    private Map<String, List<Fishes>> seasonalFish;
    private List<Fishes> legendaryFish;

    private void initializeFish() {
        seasonalFish = new HashMap<>();
        legendaryFish = new ArrayList<>();


        List<Fishes> springFish = new ArrayList<>();
        springFish.add(new Fishes(false, "Spring", 100, "Flounder"));
        springFish.add(new Fishes(false, "Spring", 100, "Lionfish"));
        springFish.add(new Fishes(false, "Spring", 30, "Herring"));
        springFish.add(new Fishes(false, "Spring", 45, "Ghostfish"));
        seasonalFish.put("Spring", springFish);


        List<Fishes> summerFish = new ArrayList<>();
        summerFish.add(new Fishes(false, "Summer", 75, "Tilapia"));
        summerFish.add(new Fishes(false, "Summer", 100, "Dorado"));
        summerFish.add(new Fishes(false, "Summer", 30, "Sunfish"));
        summerFish.add(new Fishes(false, "Summer", 65, "Rainbow Trout"));
        seasonalFish.put("Summer", summerFish);


        List<Fishes> fallFish = new ArrayList<>();
        fallFish.add(new Fishes(false, "Fall", 75, "Salmon"));
        fallFish.add(new Fishes(false, "Fall", 40, "Sardine"));
        fallFish.add(new Fishes(false, "Fall", 60, "Shad"));
        fallFish.add(new Fishes(false, "Fall", 120, "Blue Discus"));
        seasonalFish.put("Fall", fallFish);


        List<Fishes> winterFish = new ArrayList<>();
        winterFish.add(new Fishes(false, "Winter", 150, "Midnight Carp"));
        winterFish.add(new Fishes(false, "Winter", 80, "Squid"));
        winterFish.add(new Fishes(false, "Winter", 100, "Tuna"));
        winterFish.add(new Fishes(false, "Winter", 55, "Perch"));
        seasonalFish.put("Winter", winterFish);


        legendaryFish.add(new Fishes(true, "Spring", 5000, "Legend"));
        legendaryFish.add(new Fishes(true, "Winter", 1000, "Glacierfish"));
        legendaryFish.add(new Fishes(true, "Fall", 900, "Angler"));
        legendaryFish.add(new Fishes(true, "Summer", 1500, "Crimsonfish"));
    }


    public List<Fishes> getFishForSeason(String season) {
        return seasonalFish.getOrDefault(season, new ArrayList<>());
    }

    public List<Fishes> getLegendaryFish() {
        return new ArrayList<>(legendaryFish);
    }

    public Fishes getFishByName(String name) {
        for (List<Fishes> fishList : seasonalFish.values()) {
            for (Fishes fish : fishList) {
                if (fish.getName().equals(name)) {
                    return fish;
                }
            }
        }


        for (Fishes fish : legendaryFish) {
            if (fish.getName().equals(name)) {
                return fish;
            }
        }

        return null;
    }

    public List<Fishes> getAllFish() {
        List<Fishes> allFish = new ArrayList<>();

        for (List<Fishes> fishList : seasonalFish.values()) {
            allFish.addAll(fishList);
        }

        allFish.addAll(legendaryFish);

        return allFish;
    }

}
