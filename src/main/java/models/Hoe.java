package models;

import models.Enums.SkillType;

public class Hoe {
    private static final int BASE_ENERGY_COST = 10;
    private static final int FARMING_XP_REWARD = 5;

    public void use(Player player) {
        int energyCost = calculateEnergyCost(player);

        if (player.consumeEnergy(energyCost)) {
            System.out.println("Hoe used! Energy cost: " + energyCost);
            player.addSkillXp(SkillType.FARMING, FARMING_XP_REWARD);
        } else {
            System.out.println("Not enough energy!");
        }
    }

    private int calculateEnergyCost(Player player) {
        int skillLevel = player.getSkillLevel(SkillType.FARMING);
        return Math.max(1, BASE_ENERGY_COST - skillLevel);
    }
}
