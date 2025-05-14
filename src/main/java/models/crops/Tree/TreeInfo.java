package models.crops.Tree;

import models.Enums.Season;
import models.GameSession;
import models.crops.AllCropsLoader;
import models.crops.Crop.CropInfo;
import models.crops.Seed;

public class TreeInfo {
    private final String name;
    private final String source;
    private final int[] stages;
    private final int totalHarvestDay;
    private final Fruit fruit;
    private final Season[] season;

    public TreeInfo(String name, String source, int[] stages,
                    int totalHarvestDay, String fruitName,
                    int fruitHarvestCycle, int fruitBasePrice, boolean fruitIsEdible,
                    int fruitEnergy, String[] seasons) {
        this.source = source; //AllCropsLoader.getInstance().findSeed(fruitName);
        this.name = name;
        this.stages = stages;
        this.totalHarvestDay = totalHarvestDay;
        this.fruit = new Fruit(fruitName, this, fruitHarvestCycle,
                fruitBasePrice, fruitIsEdible, fruitEnergy);
        this.season = CropInfo.extractSeasonsFromString(seasons);
    }

    public String getName() {
        return name;
    }

//    public Seed getSource() {
//        return source;
//    }

    public int[] getStages() {
        return stages;
    }

    public int getTotalHarvestDay() {
        return totalHarvestDay;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public Season[] getSeason() {
        return season;
    }
}
