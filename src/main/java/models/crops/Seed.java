package models.crops;

import models.Enums.Season;
import models.Item;

public class Seed {
    private String name;
    private final Season season;

    Seed(String name, Season season) {
        this.name = name;
        this.season = season;
    }

    public Season getSeason() {
        return season;
    }
    public String getName() {
        return name;
    }
}
