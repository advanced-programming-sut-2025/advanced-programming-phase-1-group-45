package models.MapElement.crops.Plant;

import models.Enums.Season;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.PlantSeed;
import models.Tools.Backpack.BackpackType;

public class PlantInfo {
    private final String name;
    private final String source;
    private final int[] stages;
    private final int totalHarvestTime;
    private final boolean isOneTime;
    private final Integer regrowthTime;
    private final Long baseSellPrice;
    private final boolean isEdible;
    private final Integer energy;
    private final Season[] season;
    private final boolean canBecomeGiant;

    public PlantInfo(String name, String source, int[] stages,
                     int totalHarvestTime, boolean isOneTime, Integer regrowthTime,
                     long baseSellPrice, boolean isEdible, int energy,
                     String[] seasons, boolean canBecomeGiant) {
        this.name = name;
        this.source = source;
        this.stages = stages;
        this.totalHarvestTime = totalHarvestTime;
        this.isOneTime = isOneTime;
        this.regrowthTime = regrowthTime;
        this.baseSellPrice = baseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        this.season = extractSeasonsFromString(seasons);
        this.canBecomeGiant = canBecomeGiant;
        AllCropsLoader.allPlantSeeds.add(new PlantSeed(this));
    }

    public String getName() {
        return name;
    }

    public String getSource() {
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

    public int getRegrowthTime() {
        return regrowthTime;
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


    public static Season[] extractSeasonsFromString(String[] seasons) {
        if (seasons.length == 1 && seasons[0].equalsIgnoreCase("special")) {
            return new Season[]{
                    Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER
            };
        }
        Season[] seasonArray = new Season[seasons.length];
        for (int i = 0; i < seasons.length; i++) {
            seasonArray[i] = Season.valueOf(seasons[i].toUpperCase());
        }
        return seasonArray;
    }
}
