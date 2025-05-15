package models;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private String type; // "Coop" یا "Barn"
    private String level; // "Regular", "Big", "Deluxe"
    private int x;
    private int y;
    private int width;
    private int height;
    private int capacity;
    private List<String> animals;

    public Building(String type, String level, int x, int y) {
        this.type = type;
        this.level = level;
        this.x = x;
        this.y = y;

        // تنظیم ابعاد بر اساس نوع ساختمان
        if (type.equals("Coop")) {
            this.width = 7;
            this.height = 4;
        } else { // Barn
            this.width = 7;
            this.height = 6;
        }

        // تنظیم ظرفیت بر اساس سطح
        setCapacityByLevel();

        // مقداردهی اولیه لیست حیوانات
        animals = new ArrayList<>();
    }

    // تنظیم ظرفیت بر اساس سطح
    private void setCapacityByLevel() {
        if (type.equals("Coop")) {
            switch (level) {
                case "Regular":
                    capacity = 4;
                    break;
                case "Big":
                    capacity = 8;
                    break;
                case "Deluxe":
                    capacity = 12;
                    break;
            }
        } else { // Barn
            switch (level) {
                case "Regular":
                    capacity = 4;
                    break;
                case "Big":
                    capacity = 8;
                    break;
                case "Deluxe":
                    capacity = 12;
                    break;
            }
        }
    }

    // بررسی ام‌کان اضافه کردن حیوان
    public boolean canAddAnimal() {
        return animals.size() < capacity;
    }

    // اضافه کردن حیوان
    public boolean addAnimal(String animalName) {
        if (!canAddAnimal()) {
            return false;
        }

        animals.add(animalName);
        return true;
    }

    // حذف حیوان
    public boolean removeAnimal(String animalName) {
        return animals.remove(animalName);
    }

    // ارتقای ساختمان
    public boolean upgrade() {
        if (level.equals("Regular")) {
            level = "Big";
            setCapacityByLevel();
            return true;
        } else if (level.equals("Big")) {
            level = "Deluxe";
            setCapacityByLevel();
            return true;
        }
        return false; // ساختمان در بالاترین سطح است
    }

    // بررسی اینکه آیا مختصات داخل ساختمان است
    public boolean isCoordinateInside(int checkX, int checkY) {
        return checkX >= x && checkX < x + width &&
                checkY >= y && checkY < y + height;
    }

    // بررسی اینکه آیا ساختمان می‌تواند این نوع حیوان را نگهداری کند
    public boolean canHouseAnimalType(String animalType) {
        if (type.equals("Coop")) {
            return animalType.equals("Chicken") ||
                    animalType.equals("Duck") ||
                    (level.equals("Big") || level.equals("Deluxe")) && animalType.equals("Rabbit");
        } else { // Barn
            if (animalType.equals("Cow")) {
                return true;
            } else if ((level.equals("Big") || level.equals("Deluxe")) &&
                    (animalType.equals("Goat") || animalType.equals("Sheep"))) {
                return true;
            } else if (level.equals("Deluxe") && animalType.equals("Pig")) {
                return true;
            }
            return false;
        }
    }

    // اطلاعات ساختمان
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Type: ").append(type).append("\n");
        info.append("Level: ").append(level).append("\n");
        info.append("Capacity: ").append(animals.size()).append("/").append(capacity).append("\n");
        info.append("Location: (").append(x).append(", ").append(y).append(")\n");
        info.append("Animals:\n");

        if (animals.isEmpty()) {
            info.append("  None");
        } else {
            for (String animal : animals) {
                info.append("  ").append(animal).append("\n");
            }
        }

        return info.toString();
    }

    // گترها
    public String getType() {
        return type;
    }

    public String getLevel() {
        return level;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return animals.size();
    }

    public List<String> getAnimals() {
        return new ArrayList<>(animals);
    }
}
