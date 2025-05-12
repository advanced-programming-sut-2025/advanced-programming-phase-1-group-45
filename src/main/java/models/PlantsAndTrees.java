package models;

import models.Enums.Season;
import models.Enums.Weather;

public class PlantsAndTrees {
  //  private static PlantType[] plantType;
    //private static TreeType[] treeType;
    private String name;
    private Energy energy;
    private Season season;
    private Weather weather;
    private String source;
    private int stage;
    private int timeToCompleteGrowth;
    private String takenSeveralTimes;
    private int harvestingIntervals;
    private int price;
    private boolean eatable;
    private boolean hugeProduct;
    private String fruit;
    private String coal;

    public String harvestFruit() {
        return fruit;
    }
    public String treeIsBurned(){
        if(weather.lightning){
            return coal;
        }
    }
    public void crowAttack(){}
}
