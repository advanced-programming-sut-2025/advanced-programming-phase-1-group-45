package models;

import models.Enums.SkillType;

public class Skill {
    private int currentXp;
    private int level;
    private final SkillType type;

    public Skill(SkillType type) {
        this.type = type;
        this.level = 0;
        this.currentXp = 0;
    }


    public void addXp(int xp) {
        currentXp += xp;
        checkLevelUp();
    }


    private void checkLevelUp() {
        int xpNeeded = 50 + (level * 100);
        if (currentXp >= xpNeeded && level < 4) {
            level++;
            currentXp = 0;
            System.out.println("Skill " + type + " get " + level );
        }
    }

    public int getLevel() { return level; }
    public SkillType getType() { return type; }
}
