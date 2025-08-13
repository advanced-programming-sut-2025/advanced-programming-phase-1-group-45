package com.proj.Model.Cooking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Player;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;

public class FoodItem extends InventoryItem {
    private final int energyRestored;

    public FoodItem(String id, String name, int energyRestored, String buffEffect, int buffDurationHours) {
        super(id, name, GameAssetManager.getGameAssetManager().getFoodTexture(id), true, 99);
        this.energyRestored = energyRestored;
    }

    public int getEnergyRestored() {
        return energyRestored;
    }



    @Override
    public void use() {
    }

    public void use(Player player) {
        player.restoreEnergy(energyRestored);
        TextureRegion tex = this.getTexture();
        player.startEatingAnimation(tex);
        Gdx.app.log("FoodItem", "Player consumed " + getName() + ". Energy: " + energyRestored );
    }
}

