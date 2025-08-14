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
        return 8.0f;
    }

    @Override
    public boolean useOnTile(int tileX, int tileY) {
        boolean isWaterTile = checkIfWaterTile(tileX, tileY);

        if (isWaterTile && !isCasting) {
            startCasting();
            return true;
        }
        return false;
    }

    private boolean checkIfWaterTile(int tileX, int tileY) {
        return true;
    }

    private void startCasting() {
        isCasting = true;
        castTime = 0;
    }

    public void update(float delta) {
        if (isCasting) {
            castTime += delta;
            if (castTime > 3.0f) {
                attemptCatchFish();
            }
        }
    }

    private void attemptCatchFish() {
        isCasting = false;
        castTime = 0;
    }

    @Override
    public float getEnergyCost() {
        float baseCost = super.getEnergyCost();
        switch (getLevel()) {
            case 1: return baseCost;
            case 2: return baseCost * 0.75f;
            case 3: return baseCost * 0.5f;
            case 4: return baseCost * 0.25f;
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
