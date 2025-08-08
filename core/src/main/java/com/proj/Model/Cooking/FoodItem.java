package com.proj.Model.Cooking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Player;
import com.proj.Model.Cooking.Buff;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;

public class FoodItem extends InventoryItem {
    private final int energyRestored;
    private final String buffEffect;
    private final int buffDurationHours;

    public FoodItem(String id, String name, int energyRestored, String buffEffect, int buffDurationHours) {
        super(id, name, GameAssetManager.getGameAssetManager().getFoodTexture(id), true, 99);
        this.energyRestored = energyRestored;
        this.buffEffect = buffEffect;
        this.buffDurationHours = buffDurationHours;
    }

    public int getEnergyRestored() {
        return energyRestored;
    }

    public String getBuffEffect() {
        return buffEffect;
    }

    public int getBuffDurationHours() {
        return buffDurationHours;
    }

    @Override
    public void use() {
        // This method is not called directly, but use(Player player) is called instead.
    }

    public void use(Player player) {
        player.restoreEnergy(energyRestored);
        player.startEatingAnimation();
        if (buffEffect != null && !buffEffect.isEmpty()) {
            player.applyBuff(new Buff(buffEffect, buffDurationHours, 0));
        }
        Gdx.app.log("FoodItem", "Player consumed " + getName() + ". Energy: " + energyRestored + ", Buff: " + buffEffect);
    }
}
