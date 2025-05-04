package models;

public class Season {
    private static models.Enums.Season currentSeason;

    public static models.Enums.Season getSeason() {
        return currentSeason;
    }
}
