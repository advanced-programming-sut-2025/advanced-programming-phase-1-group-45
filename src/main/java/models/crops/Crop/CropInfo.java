package models.crops.Crop;

import models.Enums.Season;
import models.Item;
import models.crops.AllCropsLoader;
import models.crops.Seed;

public class CropInfo {
    private final String name;
    private final Seed source;
    private final int[] stages;
    private final int totalHarvestTime;
    private final boolean isOneTime;
    private final int regrowTime;
    private final long baseSellPrice;
    private final boolean isEdible;
    private final int energy;
    private final Season[] season;
    private final boolean canBecomeGiant;

    CropInfo(String name, String source, int[] stages,
             int totalHarvestTime, boolean isOneTime, int regrowTime,
             long baseSellPrice, boolean isEdible, int energy,
             Season[] season, boolean canBecomeGiant) {
        this.name = name;
        this.source = AllCropsLoader.getInstance().findSeed(source);
        this.stages = stages;
        this.totalHarvestTime = totalHarvestTime;
        this.isOneTime = isOneTime;
        this.regrowTime = regrowTime;
        this.baseSellPrice = baseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        this.season = season;
        this.canBecomeGiant = canBecomeGiant;
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
    public int getTotalHarvestTime() {
        return totalHarvestTime;
    }
    public boolean isOneTime() {
        return isOneTime;
    }
    public int getRegrowTime() {
        return regrowTime;
    }
    public long getBaseSellPrice() {
        return baseSellPrice;
    }
    public boolean isEdible() {
        return isEdible;
    }
    public int getEnergy() {
        return energy;
    }
    public Season[] getSeason() {
        return season;
    }
    public boolean isCanBecomeGiant() {
        return canBecomeGiant;
    }
}
