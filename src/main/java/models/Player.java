package models;

import models.Enums.SkillType;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private int energy;
    private final int maxEnergy;
    private final Map<SkillType, Integer> skills;
    private final Map<SkillType, Integer> skillXp;

    public Player(int maxEnergy) {
        this.energy = maxEnergy;
        this.maxEnergy = maxEnergy;
        this.skills = new HashMap<>();
        this.skillXp = new HashMap<>();
        initializeSkills();
    }

    private void initializeSkills() {
        for (SkillType skill : SkillType.values()) {
            skills.put(skill, 0);
            skillXp.put(skill, 0);
        }
    }


    public void addSkillXp(SkillType skill, int xp) {
        int currentXp = skillXp.get(skill);
        int currentLevel = skills.get(skill);


        if (currentLevel < 4) {
            skillXp.put(skill, currentXp + xp);

            int xpNeeded = 50 + (currentLevel * 100);
            if (skillXp.get(skill) >= xpNeeded) {
                skills.put(skill, currentLevel + 1);
                skillXp.put(skill, 0);
                System.out.println(skill + "get " + (currentLevel + 1) );
            }
        }
    }


    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            return true;
        }
        return false;
    }

    public void restoreEnergy() {
        energy = maxEnergy;
    }

    public int getEnergy() { return energy; }
    public int getSkillLevel(SkillType skill) { return skills.get(skill); }
}