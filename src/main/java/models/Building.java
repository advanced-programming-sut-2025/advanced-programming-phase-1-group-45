package models;

import java.util.ArrayList;
import java.util.List;

public class Building {
    private String type;
    private String level;
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

        if (type.equals("Coop")) {
            this.width = 7;
            this.height = 4;
        } else {
            this.width = 7;
            this.height = 6;
        }

        setCapacityByLevel();
        animals = new ArrayList<>();
    }

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
        } else {
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

    public boolean canAddAnimal() {
        return animals.size() < capacity;
    }

    public boolean addAnimal(String animalName) {
        if (!canAddAnimal()) {
            return false;
        }

        animals.add(animalName);
        return true;
    }

    public boolean removeAnimal(String animalName) {
        return animals.remove(animalName);
    }

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
        return false;
    }

    public boolean isCoordinateInside(int checkX, int checkY) {
        return checkX >= x && checkX < x + width &&
                checkY >= y && checkY < y + height;
    }

    public boolean canHouseAnimalType(String animalType) {
        if (type.equals("Coop")) {
            return animalType.equals("Chicken") ||
                    animalType.equals("Duck") ||
                    (level.equals("Big") || level.equals("Deluxe")) && animalType.equals("Rabbit");
        } else {
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
