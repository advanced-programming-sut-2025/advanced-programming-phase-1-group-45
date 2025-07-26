package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FishingRod extends Tool {
    private boolean isCasting;
    private float castTime;

    public FishingRod(String id, String name, TextureRegion texture, int level) {
        super(id, name, texture, ToolType.FISHING_ROD, level);
        this.isCasting = false;
        this.castTime = 0;
    }

    @Override
    protected float getBaseEnergyCost() {
        return 8.0f; // Base energy cost for Fishing Rod
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        // Check if the tile is water
        boolean isWaterTile = checkIfWaterTile(tileX, tileY);

        if (isWaterTile && !isCasting) {
            startCasting();
            return true;
        }
        return false;
    }

    private boolean checkIfWaterTile(int tileX, int tileY) {
        // Logic to check if the tile is water
        // This would interact with the game's tile system
        return true; // Placeholder
    }

    private void startCasting() {
        isCasting = true;
        castTime = 0;
    }

    public void update(float delta) {
        if (isCasting) {
            castTime += delta;
            // After some time, there's a chance to catch fish
            if (castTime > 3.0f) { // 3 seconds example
                attemptCatchFish();
            }
        }
    }

    private void attemptCatchFish() {
        // Logic for catching fish based on level and other factors
        isCasting = false;
        castTime = 0;
    }

    @Override
    public float getEnergyCost() {
        // Adjust energy cost based on rod type
        float baseCost = super.getEnergyCost();
        switch (getLevel()) {
            case 1: return baseCost;          // Training Rod
            case 2: return baseCost * 0.75f;  // Bamboo Rod
            case 3: return baseCost * 0.5f;   // Fiberglass Rod
            case 4: return baseCost * 0.25f;  // Iridium Rod
            default: return baseCost;
        }
    }

    public boolean isCasting() {
        return isCasting;
    }

    public void cancelCasting() {
        isCasting = false;
        castTime = 0;
    }
}
