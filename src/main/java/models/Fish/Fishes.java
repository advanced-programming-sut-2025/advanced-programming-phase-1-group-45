package models.fish;

public class Fishes {
    private String name;
    private int basePrice;
    private String season;
    private boolean isLegendary;

    public Fishes(boolean isLegendary, String season, int basePrice, String name) {
        this.isLegendary = isLegendary;
        this.season = season;
        this.basePrice = basePrice;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isLegendary() {
        return isLegendary;
    }

    public String getSeason() {
        return season;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public int getSellPrice(String quality) {
        double multiplier;
        switch (quality) {
            case "Silver":
                multiplier = 1.25;
                break;
            case "Gold":
                multiplier = 1.5;
                break;
            case "Iridium":
                multiplier = 2.0;
                break;
            default:
                multiplier = 1.0;
        }
        return (int)(basePrice * multiplier);
    }

    public String toString() {
        return name + " (" + basePrice + "g)";
    }
}
