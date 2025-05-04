package models;

import models.Enums.AnimalType;
//import models.Enums.Shop;
import models.Enums.Weather;

public class Animal {
    private AnimalType animal;
    private String name;
    private int friendshipLevel;
    private AnimalType type;
    private Friendship friendship;
   // private Shop[] shops;
    private MovementController movementController;
    private Weather weather;
    private PlantsAndTrees[] plantsAndTrees;
    public void feed(){}
    private int price;
    public static void collectProduct(){}
}
