package models.Animal;

import java.util.Random;

public class Animal {

    public String name;
    public String type;
    public int friendship;
    public boolean wasPettedToday;
    public boolean wasFedToday;
    public boolean wasOutsideToday;
    private boolean produceCollected;
    public Random random = new Random();

    public Animal(String name, String type) {
        this.name = name;
        this.type = type;
        this.friendship = 0;
        this.wasPettedToday = false;
        this.wasFedToday = false;
        this.wasOutsideToday = false;
        this.produceCollected = true;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getFriendship() {
        return friendship;
    }

    public boolean isWasPettedToday() {
        return wasPettedToday;
    }

    public boolean isWasFedToday() {
        return wasFedToday;
    }

    public  boolean isWasOutsideToday() {
        return wasOutsideToday;
    }

    public boolean isProduceCollected() {
        return produceCollected;
    }


    public void pet() {
        if (!wasPettedToday) {
            friendship += 15;
            wasPettedToday = true;
            System.out.println("You pet " + name + ".");
        } else {
            System.out.println("You've already pet " + name + " today.");
        }
        capFriendship();
    }


    public ProductInfo collectProduct() {
        if (produceCollected) {
            System.out.println("Product is collected");
        }

        if (!wasFedToday) {
            System.out.println("No food, no product");
        }

        produceCollected = true;
        friendship += 5;
        capFriendship();


        String product = getBaseProduct();


        if (canProduceSpecialProduct() && shouldProduceSpecial()) {
            product = getSpecialProduct();
        }


        String quality = determineProductQuality();

        return new ProductInfo(product, quality);
    }


    private boolean canProduceSpecialProduct() {
        return friendship >= 100 &&
                (type.equals("Cow") || type.equals("Goat") ||
                        type.equals("Sheep") || type.equals("Duck"));
    }


    private boolean shouldProduceSpecial() {
        double chance = (friendship / 1500.0) + (random.nextDouble() * 0.5 + 0.5);
        return chance > 1.0;
    }


    public void feedHay() {
        if (!wasFedToday) {
            wasFedToday = true;
            System.out.println("You fed " + name + " with hay.");
        } else {
            System.out.println(name + " has already been fed today.");
        }
    }


    public void goOutside() {
        wasOutsideToday = true;
        if (!wasFedToday) {
            wasFedToday = true;
            friendship += 8;
            capFriendship();
        }
        System.out.println(name + " went outside and ate fresh grass.");
    }


    public void goInside() {
        wasOutsideToday = false;
        System.out.println(name + " went inside.");
    }


    private String getBaseProduct() {
        switch (type) {
            case "Cow":
                return "Milk";
            case "Goat":
                return "Goat Milk";
            case "Sheep":
                return "Wool";
            case "Chicken":
                return "Egg";
            case "Duck":
                return "Duck Egg";
            case "Rabbit":
                return "Rabbit's Foot";
            case "Pig":
                return "Truffle";
            default:
                return "Unknown Product";
        }
    }


    private String getSpecialProduct() {
        switch (type) {
            case "Cow":
                return "Large Milk";
            case "Goat":
                return "Large Goat Milk";
            case "Sheep":
                return "Large Wool";
            case "Duck":
                return "Duck Feather";
            default:
                return getBaseProduct();
        }
    }


    private String determineProductQuality() {
        double qualityValue = (random.nextDouble() * 0.5 + 0.5) * (friendship / 1000.0);

        if (qualityValue > 0.9) {
            return "Iridium";
        } else if (qualityValue > 0.7) {
            return "Gold";
        } else if (qualityValue > 0.5) {
            return "Silver";
        } else {
            return "Regular";
        }
    }

    public void onDayEnd() {
        if (!wasPettedToday) {
            friendship -= 10 * (200 / Math.max(1, friendship));
        }

        if (!wasFedToday) {
            friendship -= 20;
        }

        if (wasOutsideToday) {
            friendship -= 20;
        }

        wasPettedToday = false;
        wasFedToday = false;
        produceCollected = false;

        capFriendship();
    }

    // محدود کردن میزان دوستی بین 0 و 1000
    private void capFriendship() {
        if (friendship > 1000) {
            friendship = 1000;
        } else if (friendship < 0) {
            friendship = 0;
        }
    }

    // تنظیم سطح دوستی (چیت کد)
    public void setFriendship(int value) {
        friendship = value;
        capFriendship();
    }

    // گرفتن اطلاعات نمایشی حیوان
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Name: ").append(name).append("\n");
        info.append("Type: ").append(type).append("\n");
        info.append("Friendship: ").append(friendship).append("/1000\n");
        info.append("Petted today: ").append(wasPettedToday ? "Yes" : "No").append("\n");
        info.append("Fed today: ").append(wasFedToday ? "Yes" : "No").append("\n");
        info.append("Outside: ").append(wasOutsideToday ? "Yes" : "No").append("\n");
        info.append("Product: ").append(produceCollected ? "Collected" : "Available");

        return info.toString();
    }
}
