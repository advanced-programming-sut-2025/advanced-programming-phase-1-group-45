package models.Enums;

public enum Season {
    SPRING, SUMMER, FALL, WINTER;
    private int cropGrowthModifier;
    private List<Crop> availableCrops;
}
