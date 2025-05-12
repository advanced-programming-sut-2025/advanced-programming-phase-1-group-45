package managers;

public class SkillManager{
    private String name;
    private int experience;
    private int level;
    private static final int MAX_LEVEL = 4;

    public SkillManager(String name) {
        this.name = name;
        this.experience = 0;
        this.level = 0;
    }

    public void increaseExperience(int amount) {
        experience += amount;
        updateLevel();
    }

    private void updateLevel() {
        int newLevel = 0;
        for (int i = 1; i <= MAX_LEVEL; i++) {
            int requiredXP = 50 + (i * 100);
            if (experience >= requiredXP) {
                newLevel = i;
            } else {
                break;
            }
        }

        if (newLevel > level) {
            level = newLevel;
            System.out.println(name + " skill increased to level " + level + "!");
        }
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getXPForNextLevel() {
        if (level >= MAX_LEVEL) {
            return -1;
        }
        return 50 + ((level + 1) * 100);
    }

    public String getName() {
        return name;
    }
}
