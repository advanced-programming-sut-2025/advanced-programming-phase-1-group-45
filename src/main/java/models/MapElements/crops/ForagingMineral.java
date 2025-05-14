package models.MapElement.crops;

public class ForagingMineral {
    private String name;
    private String description;
    private int sellPrice;
    public ForagingMineral(String name, String description, int sellPrice) {
        this.name = name;
        this.description = description;
        this.sellPrice = sellPrice;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getSellPrice() {
        return sellPrice;
    }
}
