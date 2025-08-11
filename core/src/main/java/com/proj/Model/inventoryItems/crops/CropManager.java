package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.map.GameMap;
import com.proj.map.Season;
import com.proj.Model.Inventory.InventoryItem;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.math.MathUtils.random;

public class CropManager {
    private final Array<Crop> crops = new Array<>();
    private final Array<GiantCrop> giantCrops = new Array<>();
    private GameMap map;
    private TextureRegion waterEffectTexture;
    private TextureRegion fertilizeEffectTexture;

    private final Map<Crop, Float> waterEffectTimers = new HashMap<>();
    private final Map<GiantCrop, Float> giantCropWaterEffectTimers = new HashMap<>();
    private final Map<Crop, Float> fertilizeEffectTimers = new HashMap<>();
    private final Map<Crop, Particle[]> fertilizeParticles = new HashMap<>();
    private final Map<GiantCrop, Float> giantCropFertilizeEffectTimers = new HashMap<>();
    private final Map<GiantCrop, ParticleEmitter.Particle[]> giantCropFertilizeParticles = new HashMap<>();
    private Texture blackParticleTexture;

    private static class Particle {
        float startX;
        float startY;
        float targetY;
        float currentY;
        float speed;
        boolean active;
    }

    public void setMap(GameMap map) {
        this.map = map;
        waterEffectTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/drop_water.png")));
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1); // سیاه
        pixmap.fill();
        blackParticleTexture = new Texture(pixmap);
        pixmap.dispose();    }

    public boolean plantFromSeed(String cropId, int tileX, int tileY) {
        CropData data = CropRegistry.getInstance().get(cropId);
        if (data == null) {
            System.err.println("Crop " + cropId + " not found");
            return false;
        }

        if (!map.canPlantInTile(tileX, tileY)) {
            return false;
        }

        crops.add(new Crop(data, tileX, tileY));
        map.getTile(tileX, tileY).setOccupied(true);
        map.getTile(tileX, tileY).setPassable(false);
        checkForGiantCrops();
        return true;
    }

    public void updateDaily(Season season) {
        for (int i = 0; i < crops.size; i++) {
            Crop crop = crops.get(i);
            checkForWater(crop);
            if (crop.getCropData().getSeason().contains(season, false)) {
                crop.grow();
            }
        }

        for (int i = 0; i < giantCrops.size; i++) {
            GiantCrop crop = giantCrops.get(i);
            checkForWater(crop);
            if (crop.getCropData().getSeason().contains(season, false)) {
                crop.grow();
            }
        }
    }

    private void checkForWater(Crop crop) {
        if (!crop.isWatered()) {
            crop.increaseDaysWithoutWater();
        }
        if (crop.getDaysWithoutWater() >= 2 && !crop.isFullyGrown()) {
            map.getTile(crop.getPosition().x, crop.getPosition().y).setOccupied(false);
            map.getTile(crop.getPosition().x, crop.getPosition().y).setPassable(true);
            removeCropAt(crop.getX(), crop.getY());
            return;
        }
        crop.setIsWatered(false);
    }

    private void growDaily(Crop crop, Season season) {
        if (crop.getCropData().getSeason().contains(season, false)) {
            crop.grow();
        }

        if (crop.isFullyGrown() &&
            crop.getCropData().getSeason().contains(season, false) &&
            !crop.hasProduct()) {
            crop.setHasProduct(true);
        }
    }


    private void checkForGiantCrops() {
        for (int x = 0; x < map.getMapWidth() - 1; x++) {
            for (int y = 0; y < map.getMapHeight() - 1; y++) {
                if (canFormGiantCrop(x, y)) {
                    formGiantCrop(x, y);
                }
            }
        }
    }

    private boolean canFormGiantCrop(int x, int y) {
        Crop crop1 = getCropAt(x, y);
        Crop crop2 = getCropAt(x + 1, y);
        Crop crop3 = getCropAt(x, y + 1);
        Crop crop4 = getCropAt(x + 1, y + 1);

        return crop1 != null && crop2 != null && crop3 != null && crop4 != null &&
            crop1.getCropData().equals(crop2.getCropData()) &&
            crop1.getCropData().equals(crop3.getCropData()) &&
            crop1.getCropData().equals(crop4.getCropData()) &&
//            crop1.isFullyGrown() && crop2.isFullyGrown() &&
//            crop3.isFullyGrown() && crop4.isFullyGrown() &&
            crop1.getCropData().canBecomeGiant();
    }

    private void formGiantCrop(int x, int y) {
        Crop baseCrop = getCropAt(x, y);
        int giantStage = Math.max(getCropAt(x, y).getStage(), getCropAt(x, y + 1).getStage());
        giantStage = Math.max(giantStage, getCropAt(x + 1, y).getStage());
        giantStage = Math.max(giantStage, getCropAt(x + 1, y + 1).getStage());

        boolean giantWater = getCropAt(x, y).isWatered() || getCropAt(x, y + 1).isWatered() ||
            getCropAt(x + 1, y + 1).isWatered() || getCropAt(x + 1, y).isWatered();
        boolean giantFertilized = getCropAt(x, y).isFertilized() || getCropAt(x, y + 1).isFertilized()
            || getCropAt(x + 1, y).isFertilized() || getCropAt(x + 1, y + 1).isFertilized();

        removeCropAt(x, y);
        removeCropAt(x + 1, y);
        removeCropAt(x, y + 1);
        removeCropAt(x + 1, y + 1);

        GiantCrop giantCrop = new GiantCrop(baseCrop.getCropData(), x, y);
        giantCrop.setStage(giantStage);
        if (giantWater) {
            giantCrop.water();
        }
        if (giantFertilized) {
            giantCrop.fertilize("Basic_Fertilize");
        }
        giantCrops.add(giantCrop);

        map.getTile(x, y).setOccupied(true);
        map.getTile(x + 1, y).setOccupied(true);
        map.getTile(x, y + 1).setOccupied(true);
        map.getTile(x + 1, y + 1).setOccupied(true);
    }

    private void removeCropAt(int x, int y) {
        Crop crop = getCropAt(x, y);
        if (crop instanceof GiantCrop) {
            GiantCrop giantCrop = (GiantCrop) crop;
            giantCropWaterEffectTimers.remove(giantCrop);
            giantCrops.removeValue(giantCrop, true);
        } else if (crop != null) {
            waterEffectTimers.remove(crop);
            crops.removeValue(crop, true);
        }
    }

    public GiantCrop getGiantCropAt(int x, int y) {
        for (GiantCrop giantCrop : giantCrops) {
            Point pos = giantCrop.getPosition();
            if (x >= pos.x && x <= pos.x + 1 && y >= pos.y && y <= pos.y + 1) {
                return giantCrop;
            }
        }
        return null;
    }

    private InventoryItem harvestGiantCrop(GiantCrop giantCrop) {
        InventoryItem item = giantCrop.harvest();
        int x = giantCrop.getX();
        int y = giantCrop.getY();
        map.getTile(x, y).setOccupied(false);
        map.getTile(x + 1, y).setOccupied(false);
        map.getTile(x, y + 1).setOccupied(false);
        map.getTile(x + 1, y + 1).setOccupied(false);

        map.getTile(x, y).setPassable(true);
        map.getTile(x + 1, y).setPassable(true);
        map.getTile(x, y + 1).setPassable(true);
        map.getTile(x + 1, y + 1).setPassable(true);

        giantCrops.removeValue(giantCrop, true);

        return item;
    }

    private void renderGiantCrop(SpriteBatch batch, GiantCrop giantCrop, int tileW, int tileH) {
        TextureRegion tex = giantCrop.getTexture();
        if (tex == null) return;
        if (giantCrop.isFullyGrown()) {
            float drawX = giantCrop.getX() * tileW;
            float drawY = giantCrop.getY() * tileH;
            float width = 2 * tileW;
            float height = 2 * tileH;
            batch.draw(tex, drawX, drawY, width, height);
        } else {
            float drawX = giantCrop.getX() * tileW;
            float drawY = giantCrop.getY() * tileH;
            float width = 2 * tileW;
            float height = 2 * tileH;
            batch.draw(tex, drawX, drawY, width, height);
        }
    }


    public InventoryItem harvestAt(int tileX, int tileY) {
        GiantCrop giantCrop = getGiantCropAt(tileX, tileY);
        if (giantCrop != null) {
            return harvestGiantCrop(giantCrop);
        }
        Crop crop = getCropAt(tileX, tileY);
        if (crop == null || !crop.hasProduct()) return null;
        InventoryItem item = crop.harvest();
        if (crop.getCropData().getRegrowthTime() <= 0) {
            map.getTile(tileX, tileY).setOccupied(false);
            map.getTile(tileX, tileY).setPassable(true);
            crops.removeValue(crop, true);
        }

        return item;
    }

    public InventoryItem chopAt(int tileX, int tileY) {
        GiantCrop giantCrop = getGiantCropAt(tileX, tileY);
        if (giantCrop != null) {
            return chopGiantCrop(giantCrop);
        }
        Crop crop = getCropAt(tileX, tileY);
        if (crop == null) return null;
        return chopCrop(crop, tileX, tileY);
    }

    private InventoryItem chopCrop(Crop crop, int tileX, int tileY) {
        InventoryItem item = crop.chop();
        map.getTile(tileX, tileY).setOccupied(false);
        map.getTile(tileX, tileY).setPassable(true);
        crops.removeValue(crop, true);
        return item;
    }

    private InventoryItem chopGiantCrop(GiantCrop giantCrop) {
        InventoryItem item = null;
        if (giantCrop.isFullyGrown()) {
            item = giantCrop.harvest();
        }
        int x = giantCrop.getX();
        int y = giantCrop.getY();
        map.getTile(x, y).setOccupied(false);
        map.getTile(x + 1, y).setOccupied(false);
        map.getTile(x, y + 1).setOccupied(false);
        map.getTile(x + 1, y + 1).setOccupied(false);

        map.getTile(x, y).setPassable(true);
        map.getTile(x + 1, y).setPassable(true);
        map.getTile(x, y + 1).setPassable(true);
        map.getTile(x + 1, y + 1).setPassable(true);
        giantCrops.removeValue(giantCrop, true);
        return item;
    }

    public void renderAll(SpriteBatch batch, Season season) {
        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        for (Crop crop : crops) {
            TextureRegion tex = crop.getTexture();

            if (tex == null) {
                continue;
            }

            float texW = tex.getRegionWidth() / 2f;
            float texH = tex.getRegionHeight() / 2f;

            float drawX = crop.getX() * tileW + (tileW - texW) / 2f;
            float drawY = crop.getY() * tileH;
            batch.draw(
                tex,
                drawX, drawY,
                texW, texH
            );
            if (crop.isShowingWaterEffect()) {
                float timer = waterEffectTimers.getOrDefault(crop, 0f);
                float duration = 0.5f; // Animation duration in seconds
                float progress = Math.min(1.0f, timer / duration);

                float startY = drawY + texH + 5; // Start above the plant
                float targetY = drawY; // End at tile level
                float currentY = startY - (startY - targetY) * progress;

                batch.draw(waterEffectTexture,
                    drawX + texW / 2 - 4,
                    currentY,
                    8, 8);
            }

            if (crop.isShowingFertilizeEffect()) {
                Particle[] particles = fertilizeParticles.get(crop);
                if (particles != null) {
                    for (Particle particle : particles) {
                        if (particle.active) {
                            float screenX = particle.startX * tileW;
                            float screenY = particle.currentY * tileH;
                            batch.draw(blackParticleTexture,
                                screenX - 2,
                                screenY - 2,
                                4, 4);
                        }
                    }
                }
            }
        }

        for (GiantCrop giantCrop : giantCrops) {
            renderGiantCrop(batch, giantCrop, tileW, tileH);
            if (giantCrop.isShowingWaterEffect()) {
                float timer = giantCropWaterEffectTimers.getOrDefault(giantCrop, 0f);
                float duration = 0.5f;
                float progress = Math.min(1.0f, timer / duration);

                float drawX = giantCrop.getX() * tileW;
                float drawY = giantCrop.getY() * tileH;
                float width = 2 * tileW;
                float height = 2 * tileH;

                float startY = drawY + height + 25;
                float targetY = drawY;
                float currentY = startY - (startY - targetY) * progress;

                // Center effect on giant crop
                batch.draw(waterEffectTexture,
                    drawX + width/2 - 8,
                    currentY,
                    16, 16);
            }
        }

    }

    public void update(float delta) {
        for (Crop crop : crops) {
            crop.updateEffects(delta);
            // Update water effect timer
            if (crop.isShowingWaterEffect()) {
                float currentTime = waterEffectTimers.getOrDefault(crop, 0f);
                waterEffectTimers.put(crop, currentTime + delta);
            } else {
                waterEffectTimers.remove(crop);
            }

            if (crop.isShowingFertilizeEffect()) {
                float currentTime = fertilizeEffectTimers.getOrDefault(crop, 0f);
                fertilizeEffectTimers.put(crop, currentTime + delta);

                if (!fertilizeParticles.containsKey(crop)) {
                    Particle[] particles = new Particle[4]; // 4 ذره
                    for (int i = 0; i < particles.length; i++) {
                        particles[i] = new Particle();
                        particles[i].active = false;
                    }
                    fertilizeParticles.put(crop, particles);
                }

                updateParticles(crop, delta);
            } else {
                fertilizeEffectTimers.remove(crop);
                fertilizeParticles.remove(crop);
            }
        }
        for (GiantCrop giantCrop : giantCrops) {
            giantCrop.updateEffects(delta);
            // Update water effect timer for giant crops
            if (giantCrop.isShowingWaterEffect()) {
                float currentTime = giantCropWaterEffectTimers.getOrDefault(giantCrop, 0f);
                giantCropWaterEffectTimers.put(giantCrop, currentTime + delta);
            } else {
                giantCropWaterEffectTimers.remove(giantCrop);
            }
        }

    }

    private void updateParticles(Crop crop, float delta) {
        Particle[] particles = fertilizeParticles.get(crop);
        float timer = fertilizeEffectTimers.getOrDefault(crop, 0f);

        for (Particle particle : particles) {
            if (!particle.active) {
                // فعال کردن تصادفی ذرات
                if (random.nextFloat() > 0.7f) {
                    initParticle(particle, crop);
                }
            } else {
                // به‌روزرسانی موقعیت ذره
                particle.currentY -= particle.speed * delta;

                // بررسی پایان حرکت ذره
                if (particle.currentY <= particle.targetY) {
                    particle.active = false;
                }
            }
        }
    }


    private void initParticle(Particle particle, Crop crop) {
        particle.startX = crop.getX() + 0.2f + random.nextFloat() * 0.6f;
        particle.startY = crop.getY() + 1.0f + random.nextFloat() * 0.5f;
        particle.targetY = crop.getY();
        particle.currentY = particle.startY;
        particle.speed = 0.5f + random.nextFloat() * 1.5f;
        particle.active = true;
    }

    public Crop getCropAt(int x, int y) {
        for (Crop crop : crops) {
            if (crop.getX() == x && crop.getY() == y) {
                return crop;
            }
        }
        return null;
    }

    public Array<Crop> getCrops() {
        return crops;
    }
}

