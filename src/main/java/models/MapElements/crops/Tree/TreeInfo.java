package models.MapElements.crops.Tree;

import models.Enums.Season;
import models.GameSession;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.Plant.PlantInfo;
import models.MapElements.crops.TreeSeed;
import models.Tools.Backpack.BackPackItem;

public class TreeInfo {
    private final String name;
    private final String source;
    private boolean isForaging = false;
    private final int[] stages;
    private final int totalHarvestDay;
    private final Fruit fruit;
    private final Season[] season;

    public TreeInfo(String name, String source, int[] stages,
                    int totalHarvestDay, String fruitName,
                    int fruitHarvestCycle, int fruitBasePrice, boolean fruitIsEdible,
                    int fruitEnergy, String[] seasons) {
        this.source = source;
        this.name = name;
        this.stages = stages;
        this.totalHarvestDay = totalHarvestDay;
        this.fruit = new Fruit(fruitName, this, fruitHarvestCycle,
                fruitBasePrice, fruitIsEdible, fruitEnergy);
        this.season = PlantInfo.extractSeasonsFromString(seasons);
        AllCropsLoader.getInstance().addTreeSeed(new TreeSeed(this));
    }

    public boolean isForaging() {
        return isForaging;
    }

    public void setForaging(boolean isForaging) {
        this.isForaging = isForaging;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
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
