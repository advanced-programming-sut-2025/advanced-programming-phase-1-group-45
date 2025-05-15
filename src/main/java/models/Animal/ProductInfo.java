package models.Animal;

public class ProductInfo {

    private String productName;
    private String quality;

    public ProductInfo(String productName, String quality) {
        this.productName = productName;
        this.quality = quality;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return quality + " " + productName;
    }

    // محاسبه قیمت محصول بر اساس کیفیت
    public int getPrice() {
        int basePrice = getBasePrice(productName);
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

    // قیمت پایه محصولات
    private int getBasePrice(String productName) {
        switch (productName) {
            case "Milk":
                return 100;
            case "Large Milk":
                return 150;
            case "Goat Milk":
                return 125;
            case "Large Goat Milk":
                return 200;
            case "Wool":
                return 120;
            case "Large Wool":
                return 180;
            case "Egg":
                return 50;
            case "Duck Egg":
                return 80;
            case "Duck Feather":
                return 125;
            case "Rabbit's Foot":
                return 565;
            case "Truffle":
                return 625;
            default:
                return 50;
        }
    }
}
