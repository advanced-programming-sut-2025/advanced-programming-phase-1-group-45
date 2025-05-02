package models.Enums;

public enum Weather {
    SUNNY, RAINY, STORMY, SNOWY;
    private double fishingModifier;
    private boolean preventsOutdoorActivities;
    public boolean lightning = false;
    public static void applyDailyEffects(Weather weather, Season season) {

    }
}
