package models;

import models.Enums.SkillType;

public class Farming implements Ability {
    public void plowing(){}
    public void planting(){}
    public void fertilizer(){}
    public void watering(){}
    public static void harvest(){

    }
    @Override
    public void level1(){}
    public void level2(){}
    public void level3(){}
    public void level4(){}

    public void harvestCrop(Player player) {
        player.addSkillXp(SkillType.FARMING, 5);
    }
}
