package models;
import java.util.HashMap;
import java.util.Map;
import managers.SkillManager;


public class Energy {
    private int maxEnergy;
    private int currentEnergy;
    private boolean isUnlimited;

    private Map<String, SkillManager> skills;

    public Energy(int initialEnergy) {
        this.maxEnergy = initialEnergy;
        this.currentEnergy = initialEnergy;
        this.isUnlimited = false;

        this.skills = new HashMap<>();
        skills.put("Farming", new SkillManager("Farming"));
        skills.put("Extraction", new SkillManager("Extraction"));
        skills.put("EcoTourism", new SkillManager("EcoTourism"));
        skills.put("Fishing", new SkillManager("Fishing"));
    }

    public String showEnergy() {
        return "Current Energy: " + currentEnergy + "/" + maxEnergy + (isUnlimited ? " (Unlimited)" : "");
    }

    public boolean consumeEnergy(int amount) {
        if (isUnlimited) {
            return true;
        }

        if (currentEnergy >= amount) {
            currentEnergy -= amount;
            return true;
        } else {
            return false;
        }
    }

    public void setEnergy(int value) {
        if (value > maxEnergy) {
            currentEnergy = maxEnergy;
        } else if (value < 0) {
            currentEnergy = 0;
        } else {
            currentEnergy = value;
        }
        System.out.println("Energy set to " + currentEnergy);
    }

    public void setUnlimitedEnergy() {
        isUnlimited = true;
        System.out.println("Unlimited energy activated!");
    }

    public void restoreEnergyForNewDay(boolean collapsed) {
        if (collapsed) {
            currentEnergy = (int) (maxEnergy * 0.75);
        } else {
            currentEnergy = maxEnergy;
        }
    }

    public void increaseFarmingXP(int amount) {
        skills.get("Farming").increaseExperience(amount);
    }

    public void increaseMiningXP(int amount) {
        skills.get("Mining").increaseExperience(amount);
    }

    public void increaseForagingXP(int amount) {
        skills.get("Foraging").increaseExperience(amount);
    }

    public void increaseFishingXP(int amount) {
        skills.get("Fishing").increaseExperience(amount);
    }

    public int getFarmingLevel() {
        return skills.get("Farming").getLevel();
    }

    public int getMiningLevel() {
        return skills.get("Mining").getLevel();
    }

    public int getForagingLevel() {
        return skills.get("Foraging").getLevel();
    }

    public int getFishingLevel() {
        return skills.get("Fishing").getLevel();
    }

    public void collapse() {
        currentEnergy = 0;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }

    public void addEnergy(int amount) {
        currentEnergy = Math.min(currentEnergy + amount, maxEnergy);
    }
}

