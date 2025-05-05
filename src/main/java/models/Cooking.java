package models;

import models.Tools.Backpack;

public class Cooking {
    Backpack backpack;
    public void refrigerator(){}
    public void showRecipes(){}
    public void learningCooking(){}
    public void makeFood(){
        Energy energy = new Energy();
        Backpack backpack = new Backpack();
    }
    public void eatFood(){
        Backpack backpack = new Backpack();
        Energy energy = new Energy();
    }

}
