package models.fish;

public class FishCatch {
    private String fishName;
    private int basePrice;
    private String quality;

    public FishCatch(String fishName, String quality, int basePrice) {
        this.fishName = fishName;
        this.quality = quality;
        this.basePrice = basePrice;
    }

    public String getFishName() {
        return fishName;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public String getQuality() {
        return quality;
    }

    public int getSellPrice() {
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
        return quality + " " + fishName + " (" + getSellPrice() + "g)";
    }
}
