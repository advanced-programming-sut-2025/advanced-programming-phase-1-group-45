package models.crops.Tree;

import models.Enums.Season;
import models.GameSession;
import models.crops.AllCropsLoader;
import models.crops.Seed;

public class TreeInfo {
    private final String name;
    private final Seed source;
    private final int[] stages;
    private final int totalHarvestDay;
    private final Fruit fruit;
    private final Season[] season;

    TreeInfo(String name, String source, int[] stages,
             int totalHarvestDay, String fruitName,
             int fruitHarvestCycle, int fruitBasePrice, boolean fruitIsEdible,
             int fruitEnergy, Season[] season) {
        this.source = AllCropsLoader.getInstance().findSeed(fruitName);
        this.name = name;
        this.stages = stages;
        this.totalHarvestDay = totalHarvestDay;
        this.fruit = new Fruit(fruitName, this, fruitHarvestCycle,
                fruitBasePrice, fruitIsEdible, fruitEnergy);
        this.season = season;

    }

    public String getName() {
        return name;
    }

    public Seed getSource() {
        return source;
    }

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
