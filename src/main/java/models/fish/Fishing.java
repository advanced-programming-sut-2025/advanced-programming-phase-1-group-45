package models.fish;

import models.Energy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fishing {
    public FishManager fishManager;
    public Random random = new Random();

    public Fishing(FishManager fishManager) {
        this.fishManager = fishManager;
    }

    public List<FishCatch> goFishing(String fishingPole, String season, String weather, Energy energy) {

        int energyCost = calculateEnergyCost(fishingPole, energy.getFishingLevel());


        if (!energy.consumeEnergy(energyCost)) {
            System.out.println("Not Enough Energy");
        }


        int fishingSkill = energy.getFishingLevel();
        double weatherMultiplier = getWeatherMultiplier(weather);
        int fishCount = calculateFishCount(fishingSkill, weatherMultiplier, fishingPole);


        fishCount = Math.min(fishCount, 6);


        List<FishCatch> catches = new ArrayList<>();

        for (int i = 0; i < fishCount; i++) {

            Fishes fish = selectFish(fishingPole, season, fishingSkill);


            String quality = determineFishQuality(fishingSkill, getPoleMultiplier(fishingPole), fishCount);

            catches.add(new FishCatch(fish.getName(), quality , fish.getBasePrice()));
        }


        energy.increaseFishingXP(5 * fishCount);

        return catches;
    }

    private int calculateEnergyCost(String fishingPole, int fishingSkill) {
        int baseCost;
        switch (fishingPole) {
            case "Training Rod":
                baseCost = 8;
                break;
            case "Bamboo Pole":
                baseCost = 8;
                break;
            case "Fiberglass Rod":
                baseCost = 6;
                break;
            case "Iridium Rod":
                baseCost = 4;
                break;
            default:
                baseCost = 8;
        }


        if (fishingSkill == 4) {
            baseCost -= 1;
        }

        return Math.max(1, baseCost);
    }

    private double getWeatherMultiplier(String weather) {
        switch (weather) {
            case "Sunny":
                return 0.5;
            case "Rainy":
                return 1.2;
            case "Stormy":
                return 1.5;
            default:
                return 1.0;
        }
    }

    private int calculateFishCount(int fishingSkill, double weatherMultiplier, String fishingPole) {

        double random = Math.random();
        double baseCount = 2 + (fishingSkill * 0.5) * (random * weatherMultiplier);


        if (fishingPole.equals("Training Rod")) {
            return 1;
        }

        return (int) Math.ceil(baseCount);
    }

    private Fishes selectFish(String fishingPole, String season, int fishingSkill) {
        List<Fishes> availableFish = fishManager.getFishForSeason(season);


        if (fishingPole.equals("Training Rod")) {
            return getCheapestFish(availableFish);
        }

        if (fishingSkill == 4 && Math.random() < 0.05) {
            List<Fishes> legendaryFish = fishManager.getLegendaryFish();
            for (Fishes fish : legendaryFish) {
                if (fish.getSeason().equals(season)) {
                    return fish;
                }
            }
        }


        if (!availableFish.isEmpty()) {
            return availableFish.get(random.nextInt(availableFish.size()));
        }


        return new Fishes(false, season, 5, "Seaweed");
    }

    private Fishes getCheapestFish(List<Fishes> fishList) {
        if (fishList.isEmpty()) {
            return new Fishes(false, "All", 5, "Seaweed");
        }

        Fishes cheapest = fishList.get(0);
        for (Fishes fish : fishList) {
            if (fish.getBasePrice() < cheapest.getBasePrice()) {
                cheapest = fish;
            }
        }

        return cheapest;
    }

    private String determineFishQuality(int fishingSkill, double poleMultiplier, int fishCount) {

        double qualityScore = ((fishingSkill * 2 + poleMultiplier) * Math.random()) / (7 - fishCount);

        if (qualityScore > 0.9) {
            return "Iridium";
        } else if (qualityScore > 0.7) {
            return "Gold";
        } else if (qualityScore > 0.5) {
            return "Silver";
        } else {
            return "Regular";
        }
    }

    private double getPoleMultiplier(String fishingPole) {
        switch (fishingPole) {
            case "Training Rod":
                return 0.1;
            case "Bamboo Pole":
                return 0.5;
            case "Fiberglass Rod":
                return 0.9;
            case "Iridium Rod":
                return 1.2;
            default:
                return 0.5;
        }
    }

    public FishManager getFishManager() {
        return fishManager;
    }
}
