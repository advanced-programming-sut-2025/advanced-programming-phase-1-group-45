package com.proj.map;

public enum farmName {
    STANDARD("Standard", "A simple plot of land, with a large amount of open space to design your farm."),
    RIVERLAND("Riverland", "Your farm is spread across several islands and scenic riverbanks." +
            " Fish are more common here than usual. You start with a fish smoker."),
    FOREST("ForestLand", "The woods limit your farming space." +
            " However, the bounty of the forest is nearly at your doorstep."),
    HILL_TOP("Hill_top", "Rocky terrain and a winding river make it difficult to design your farm." +
            "However, a mineral deposit provides mining opportunities."),
    WILDERNESS("Wilderness", "There's plenty of good land here, but beware... at night the monsters come out."),
    FOUR_CORNERS("FourCorners", "The land is divide into four parcels, each with its own perk." +
           "Perfect for a group!"); //,
//    MEADOWLANDS("Meadowlands", "It's not the best for growing crops, but there's a chewy blue grass" +
//        "that animals love. You start with two chickens."),
//    BEACH("Island", "Good foraging and fishing, and tons of open space. Sometimes, supply" +
//        "crates wash up on share. However, sprinklers don't work in the sandy soil.");

    private String farmName;
    private String description;

    farmName(String farmName, String description) {
        this.farmName = farmName;
        this.description = description;
    }

    public String getFarmName() {
        return farmName;
    }

    public String getDescription() {
        return description;
    }
}
