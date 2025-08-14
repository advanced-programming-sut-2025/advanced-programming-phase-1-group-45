package com.proj.Model.Animal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.Model.Animal.Animal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FeedingController {
    private static class FeedVisual {
        Animal animal;
        Texture foodTexture;
        Texture hayHopperTexture;
        Texture hayHopperFullTexture;
        float x, y;
        float hayX, hayY;
        float timer;
        boolean hayShown;
        float initialDuration = 5.0f;
        float afterHayDuration = 2.0f;

        FeedVisual(Animal animal, Texture foodTexture,
                   Texture hayHopperTexture, Texture hayHopperFullTexture) {
            this.animal = animal;
            this.foodTexture = foodTexture;
            this.hayHopperTexture = hayHopperTexture;
            this.hayHopperFullTexture = hayHopperFullTexture;
            this.timer = 0f;
            this.hayShown = false;
        }
    }

    private final List<FeedVisual> visuals = new LinkedList<>();
    private Texture defaultHayHopper;
    private Texture defaultHayHopperFull;

    public FeedingController() {
        try {
            defaultHayHopper = new Texture(Gdx.files.internal("assets/Animals/Hay_Hopper.png"));
            defaultHayHopperFull = new Texture(Gdx.files.internal("assets/Animals/Hay_Hopper_Full.png"));
        } catch (Exception e) {
            Gdx.app.error("FeedingController", "Failed to load hopper textures", e);
            defaultHayHopper = null;
            defaultHayHopperFull = null;
        }
    }

    public void startFeedingVisual(Animal animal, String foodTexturePath) {
        if (animal == null) return;
        Texture foodTex = null;
        try {
            if (foodTexturePath != null && !foodTexturePath.isEmpty()) {
                foodTex = new Texture(Gdx.files.internal(foodTexturePath));
            }
        } catch (Exception e) {
            Gdx.app.error("FeedingController", "Failed load food texture: " + foodTexturePath, e);
            foodTex = null;
        }
        FeedVisual v = new FeedVisual(animal, foodTex, defaultHayHopper, defaultHayHopperFull);
        v.x = animal.getX();
        v.y = animal.getY();
        v.hayX = v.x;
        v.hayY = v.y - (v.foodTexture != null ? v.foodTexture.getHeight() : 16) - 8;
        visuals.add(v);
    }

    public void update(float delta) {
        Iterator<FeedVisual> it = visuals.iterator();
        while (it.hasNext()) {
            FeedVisual v = it.next();
            v.timer += delta;

            // update position relative to animal direction/position
            float ax = v.animal.getX();
            float ay = v.animal.getY();
            float animalW = getAnimalWidth(v.animal);
            float animalH = getAnimalHeight(v.animal);

            switch (v.animal.getDirection()) {
                case UP:
                    v.x = ax + animalW/2f - getFoodWidth(v)/2f;
                    v.y = ay + animalH + 8;
                    v.hayX = ax + animalW/2f - getHayWidth(v)/2f;
                    v.hayY = v.y - getHayHeight(v) - 6;
                    break;
                case DOWN:
                    v.x = ax + animalW/2f - getFoodWidth(v)/2f;
                    v.y = ay - getFoodHeight(v) - 8;
                    v.hayX = ax + animalW/2f - getHayWidth(v)/2f;
                    v.hayY = v.y - getHayHeight(v) - 6;
                    break;
                case LEFT:
                    v.x = ax - getFoodWidth(v) - 8;
                    v.y = ay + animalH/2f - getFoodHeight(v)/2f;
                    v.hayX = v.x - getHayWidth(v) - 6;
                    v.hayY = v.y;
                    break;
                case RIGHT:
                default:
                    v.x = ax + animalW + 8;
                    v.y = ay + animalH/2f - getFoodHeight(v)/2f;
                    v.hayX = v.x + getFoodWidth(v) + 6;
                    v.hayY = v.y;
                    break;
            }

            if (!v.hayShown && v.timer >= v.initialDuration) {
                v.hayShown = true;
            }

            if (v.hayShown && v.timer >= v.initialDuration + v.afterHayDuration) {
                if (v.foodTexture != null) {
                    try { v.foodTexture.dispose(); } catch (Exception ignored) {}
                }
                it.remove();
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (FeedVisual v : visuals) {
            if (v.foodTexture != null) {
                batch.draw(v.foodTexture, v.x, v.y);
            }
            if (v.hayShown) {
                Texture t = v.hayHopperFullTexture != null ? v.hayHopperFullTexture : v.hayHopperTexture;
                if (t != null) batch.draw(t, v.hayX, v.hayY);
            } else {
                Texture t = v.hayHopperTexture;
                if (t != null) batch.draw(t, v.hayX, v.hayY);
            }
        }
    }

    private float getFoodWidth(FeedVisual v) { return v.foodTexture != null ? v.foodTexture.getWidth() : 16f; }
    private float getFoodHeight(FeedVisual v) { return v.foodTexture != null ? v.foodTexture.getHeight() : 16f; }
    private float getHayWidth(FeedVisual v) { return v.hayHopperTexture != null ? v.hayHopperTexture.getWidth() : 16f; }
    private float getHayHeight(FeedVisual v) { return v.hayHopperTexture != null ? v.hayHopperTexture.getHeight() : 16f; }
    private float getAnimalWidth(Animal animal) { return 64f; } // تقریب، اگر سایز دقیق دارید عوض کنید
    private float getAnimalHeight(Animal animal) { return 64f; }

    public void dispose() {
        try {
            if (defaultHayHopper != null) defaultHayHopper.dispose();
            if (defaultHayHopperFull != null) defaultHayHopperFull.dispose();
        } catch (Exception e) {
            Gdx.app.error("FeedingController", "Error disposing", e);
        }
    }
}
